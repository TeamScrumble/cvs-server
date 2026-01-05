package product.product.application.service

import com.fasterxml.jackson.databind.ObjectMapper
import cvs.crawler.CrawlerRequestEvent
import cvs.crawler.CrawlerResultEvent
import cvs.crawler.CvsTarget
import db.transactional.Transactional
import error.errorcode.ProductErrorCode
import error.exception.BusinessException
import kotlinx.coroutines.flow.firstOrNull
import member.MemberApi
import org.springframework.data.domain.Pageable
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import passport.Passport
import passport.isAdmin
import product.common.valid.MemberValidService
import product.product.application.utils.toEntity
import product.product.domain.repository.ProductCustomRepository
import product.product.domain.repository.ProductRepository
import product.product.domain.repository.SyncJobRepository
import product.product.domain.table.SyncJob
import product.product.domain.table.SyncJobStatus
import product.product.domain.table.SyncJobType
import product.product.elasticsearch.service.ProductEsService
import product.product.elasticsearch.util.toDto
import product.product.presentation.kafka.sync.ProductEsSyncRequestedEvent
import product.product.presentation.kafka.sync.ProductEsSyncTopics

@Service
class ProductService(
    private val objectMapper: ObjectMapper,
    private val transactional: Transactional,
    private val productEsService: ProductEsService,
    private val syncJobRepository: SyncJobRepository,
    private val productRepository: ProductRepository,
    private val memberValidService: MemberValidService,
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val productCustomRepository: ProductCustomRepository,
    private val memberApi: MemberApi,
) {
    suspend fun validateExists(productId: Long) = productRepository.existsById(productId)

    suspend fun sync(passport: Passport): Long {
        memberValidService.validateMember(passport)

        if (!passport.isAdmin) {
            throw BusinessException(ProductErrorCode.P_005)
        }

        return sync(passport.memberId)
    }

    /**
     * memberId가 0인 경우 스케줄러
     * */
    suspend fun sync(memberId: Long): Long {
        // 동시에 1개만 돌리려면 여기서 차단
        val active = syncJobRepository
            .findLatestActiveJob(SyncJobType.PRODUCT_ES_INITIAL_LOAD.name)
            .firstOrNull()

        // 이미 진행 중인 작업이 있으면 그 jobId를 반환
        if (active != null) {
            return active.id
        }

        val job = syncJobRepository.save(
            SyncJob(
                type = SyncJobType.PRODUCT_ES_INITIAL_LOAD,
                status = SyncJobStatus.QUEUED,
                requestedBy = memberId,
                pageSize = 2000
            )
        )

        val event = ProductEsSyncRequestedEvent(
            jobId = job.id,
            pageSize = job.pageSize
        )

        kafkaTemplate.send(
            ProductEsSyncTopics.REQUEST,
            job.id.toString(),
            objectMapper.writeValueAsString(event)
        )

        return job.id
    }

    suspend fun crawl(passport: Passport, targets: List<CvsTarget>): Boolean {
        memberValidService.validateMember(passport)

        if (!passport.isAdmin) {
            throw BusinessException(ProductErrorCode.P_002)
        }

        targets.forEach { target ->
            val payload = objectMapper.writeValueAsString(CrawlerRequestEvent(target))
            kafkaTemplate.send("crawl.request", payload)
        }

        return true
    }

    suspend fun saveAll(passport: Passport, results: List<CrawlerResultEvent>): List<Long> {
        memberValidService.validateMember(passport)

        return transactional {
            if (!passport.isAdmin) throw BusinessException(ProductErrorCode.P_002)
            results.flatMap { save(it) }.distinct()
        }
    }

    /**
     * 단일 save의 경우 스케줄러 카프카 이벤트 및 saveAll을 제외한 곳에서 호출되지 않기 때문에 따로 passport 체크가 없음
     * */
    suspend fun save(result: CrawlerResultEvent, chunkSize: Int = 1000): List<Long> {
        val products = result.data.map { it.toEntity(result.target) }

        val savedIds = mutableListOf<Long>()

        products.chunked(chunkSize).forEach { chunk ->
            savedIds += productCustomRepository.upsertAll(chunk) // upsertAll 반환 변경: Long -> List<Long>
        }

        return savedIds.distinct()
    }

    suspend fun findById(id: Long) = productRepository.findById(id)
        ?: throw BusinessException(ProductErrorCode.P_001)

    suspend fun findAllByKeyword(
        cvsTarget: CvsTarget?,
        keyword: String,
        pageable: Pageable
    ) = productEsService.findAllByKeyword(cvsTarget, keyword, pageable)
        .map { it.toDto() }
        .toList()

    suspend fun existsById(id: Long): Boolean = productRepository.existsById(id)
}