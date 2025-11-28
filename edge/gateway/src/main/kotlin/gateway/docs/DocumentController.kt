package gateway.docs

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class DocumentController(
    private val apiDocumentService: ApiDocumentService,
    private val errorDocumentService: ErrorDocumentService
) {
    @GetMapping("/oas.json")
    @ResponseBody
    suspend fun oasPublic(): Map<String, Any?> {
        return apiDocumentService.generateOasJson(publicOnly = true)
    }

    @GetMapping("/internal/oas.json")
    @ResponseBody
    suspend fun oasInternal(): Map<String, Any?> {
        return apiDocumentService.generateOasJson(publicOnly = false)
    }

    @GetMapping("/error.json")
    @ResponseBody
    suspend fun error(): Map<String, Any?> {
        return errorDocumentService.generateErrorJson()
    }
}