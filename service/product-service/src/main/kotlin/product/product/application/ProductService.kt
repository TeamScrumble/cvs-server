package product.product.application

import cvs.crawler.CrawlerData
import cvs.crawler.CrawlerResultEvent
import error.errorcode.AuthErrorCode
import error.errorcode.ProductErrorCode
import error.exception.BusinessException
import extension.getOrThrow
import kotlinx.coroutines.flow.toList
import member.MemberApi
import org.springframework.stereotype.Service
import passport.Passport
import product.product.domain.Product
import product.product.domain.ProductRepository

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val memberApi: MemberApi,
) {
    private suspend fun validateMember(passport: Passport) {
        memberApi.get(passport.memberId).getOrThrow()
    }

    suspend fun saveAll(passport: Passport, results: List<CrawlerResultEvent>): Int {
        validateMember(passport)
        return results.sumOf { save(it) }
    }

    /**
     * 단일 save의 경우 스케줄러 카프카 이벤트 및 saveAll을 제외한 곳에서 호출되지 않기 때문에 따로 passport 체크가 없음
     * */
    suspend fun save(result: CrawlerResultEvent): Int {
        val products = result.data.map { it.toEntity(result.target) }
        return productRepository.saveAll(products).toList().size
    }

    suspend fun findById(id: Long) = productRepository.findById(id)
        ?: throw BusinessException(ProductErrorCode.P_001)

    private fun CrawlerData.toEntity(target: cvs.crawler.CvsTarget): Product {
        return Product(
            id = 0L,
            cvsProductId = this.id.toLong(),
            cvsTarget = target,
            title = this.productName,
            img = this.imgUrl,
            price = this.price,
            event = this.flag,
            isNewProduct = this.isNew
        )
    }
}