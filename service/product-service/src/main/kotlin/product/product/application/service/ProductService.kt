package product.product.application.service

import com.fasterxml.jackson.databind.ObjectMapper
import cvs.crawler.CrawlerRequestEvent
import cvs.crawler.CrawlerResultEvent
import cvs.crawler.CvsTarget
import db.transactional.Transactional
import error.errorcode.ProductErrorCode
import error.exception.BusinessException
import org.springframework.data.domain.Pageable
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import passport.Passport
import passport.isAdmin
import product.common.valid.MemberValidService
import product.product.application.utils.toEntity
import product.product.domain.repository.ProductCustomRepository
import product.product.domain.repository.ProductRepository
import product.product.elasticsearch.service.ProductEsService
import product.product.elasticsearch.util.toDto

@Service
class ProductService(
    private val objectMapper: ObjectMapper,
    private val transactional: Transactional,
    private val productEsService: ProductEsService,
    private val productRepository: ProductRepository,
    private val memberValidService: MemberValidService,
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val productCustomRepository: ProductCustomRepository,
) {
    suspend fun validateExists(productId: Long) = productRepository.existsById(productId)

    suspend fun saveAll(passport: Passport, results: List<CrawlerResultEvent>): Long {
        memberValidService.validateMember(passport)

        return transactional {
            if (!passport.isAdmin) {
                throw BusinessException(ProductErrorCode.P_002)
            }

            results.sumOf { save(it) }
        }
    }

    /**
     * 단일 save의 경우 스케줄러 카프카 이벤트 및 saveAll을 제외한 곳에서 호출되지 않기 때문에 따로 passport 체크가 없음
     * */
    suspend fun save(result: CrawlerResultEvent, chunkSize: Int = 1000): Long {
        val products = result.data.map { it.toEntity(result.target) }

        var count = 0L

        // 몇 만 건의 데이터를 쌓을 경우, 메모리 폭발 여지가 있어 chunk 방식을 사용해 bulk 업데이트
        products.chunked(chunkSize).forEach { chunk ->
            count += productCustomRepository.upsertAll(chunk)
        }

        return count
    }

    suspend fun findById(id: Long) = productRepository.findById(id)
        ?: throw BusinessException(ProductErrorCode.P_001)

    suspend fun findAllByCvsTarget(cvsTarget: CvsTarget) = productRepository
        .findAllByCvsTarget(cvsTarget)

    suspend fun findAllByKeyword(
        cvsTarget: CvsTarget,
        keyword: String,
        pageable: Pageable
    ) = productEsService.findAllByKeyword(cvsTarget, keyword, pageable)
        .map { it.toDto() }
        .toList()

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

    suspend fun existsById(id: Long): Boolean = productRepository.existsById(id)
}