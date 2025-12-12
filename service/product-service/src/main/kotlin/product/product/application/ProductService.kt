package product.product.application

import cvs.crawler.CrawlerResultEvent
import cvs.crawler.CvsTarget
import db.transactional.Transactional
import error.errorcode.ProductErrorCode
import error.exception.BusinessException
import extension.getOrThrow
import member.MemberApi
import org.springframework.stereotype.Service
import passport.Passport
import product.product.domain.ProductCustomRepository
import product.product.domain.ProductRepository

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val productCustomRepository: ProductCustomRepository,
    private val memberApi: MemberApi,
    private val transactional: Transactional,
) {
    private suspend fun validateMember(passport: Passport) {
        memberApi.get(passport.memberId).getOrThrow()
    }

    suspend fun saveAll(passport: Passport, results: List<CrawlerResultEvent>): Long = transactional {
        validateMember(passport)

        results.sumOf { save(it) }
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
}