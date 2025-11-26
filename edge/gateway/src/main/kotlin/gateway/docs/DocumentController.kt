package gateway.docs

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DocumentController(
    private val apiDocumentService: ApiDocumentService,
) {
    @GetMapping("/docs")
    fun forwardDocs(): String = "forward:/docs/index.html"

    @GetMapping("/oas.json")
    suspend fun aggregate(): Map<String, Any?> {
        return apiDocumentService.generateOasJson()
    }
}