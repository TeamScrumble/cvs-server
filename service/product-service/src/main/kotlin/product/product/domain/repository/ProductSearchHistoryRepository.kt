package product.product.domain.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import product.product.domain.table.ProductSearchHistory

@Repository
interface ProductSearchHistoryRepository : CoroutineCrudRepository<ProductSearchHistory, Long>, ProductSearchHistoryRepositoryCustom

