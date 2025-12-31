package product.product.elasticsearch.bootstrap

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Component
import product.product.elasticsearch.document.ProductDocument

@Component
class ProductIndexBootstrap(
    private val operations: ElasticsearchOperations
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        val indexOps = operations.indexOps(ProductDocument::class.java)

        if (!indexOps.exists()) {
            indexOps.createWithMapping()
        }
    }
}