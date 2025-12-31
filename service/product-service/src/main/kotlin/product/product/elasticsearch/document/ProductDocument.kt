package product.product.elasticsearch.document

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Mapping
import org.springframework.data.elasticsearch.annotations.Setting

@Document(indexName = "products_v1", createIndex = true)
@Setting(settingPath = "elasticsearch/products-settings.json")
@Mapping(mappingPath = "elasticsearch/products-mapping.json")
data class ProductDocument(
    @Id
    val productId: Long,
    val cvsProductId: Long,
    val cvsTarget: String,
    val title: String,
    val img: String,
    val price: Int,
    val event: String,
    val isNewProduct: Boolean,
    val likeCount: Int
)