package docs

import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.*
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.RequestBody
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import io.swagger.v3.oas.models.responses.ApiResponse as SwaggerApiResponse

@Component
class DocumentCustomizer : OperationCustomizer {

    override fun customize(
        operation: Operation,
        handlerMethod: HandlerMethod
    ): Operation {
        val documented = handlerMethod.getMethodAnnotation(Documented::class.java)
            ?: return operation

        // 공통 메타 정보
        operation.summary = documented.summary
        operation.description = documented.description

        customizeRequest(operation, documented)
        customizePassport(operation)
        customizeResponse(operation, documented)

        return operation
    }

    /**
     * request body 스키마 커스터마이징
     */
    private fun customizeRequest(
        operation: Operation,
        documented: Documented,
    ) {
        if (documented.request == Nothing::class) return

        val schemaRef = "#/components/schemas/${documented.request.simpleName}"

        val requestBody = operation.requestBody ?: RequestBody()
        val content = requestBody.content ?: Content()
        val mediaType = content[APPLICATION_JSON_VALUE] ?: MediaType()

        mediaType.schema = Schema<Any>().apply { `$ref` = schemaRef }

        content.addMediaType(APPLICATION_JSON_VALUE, mediaType)
        requestBody.content = content
        operation.requestBody = requestBody
    }

    /**
     * passport 파라미터 제거 + Authorization 헤더 추가
     */
    private fun customizePassport(operation: Operation) {
        val hadPassportParam = operation.parameters
            ?.any { it.name?.contains("passport", ignoreCase = true) == true }
            ?: false

        // passport 파라미터 제거
        operation.parameters = operation.parameters
            ?.filterNot { parameter ->
                parameter.name?.contains("passport", ignoreCase = true) == true
            }
            ?.takeIf { it.isNotEmpty() }

        if (!hadPassportParam) return

        // 이미 Authorization 헤더가 있으면 추가 안 함
        val hasAuthHeader = operation.parameters
            ?.any { it.`in` == "header" && it.name.equals("Authorization", ignoreCase = true) }
            ?: false

        if (hasAuthHeader) return

        val authParam = Parameter().apply {
            name = "Authorization"
            `in` = "header"
            required = true
            description = "JWT Access Token (Bearer 토큰)"
            schema = StringSchema().apply {
                example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            }
        }

        operation.addParametersItem(authParam)
    }

    /**
     * response 스키마를 ApiResponse.Success<T> 형태로 감싸기
     */
    private fun customizeResponse(
        operation: Operation,
        documented: Documented,
    ) {
        if (documented.response == Nothing::class) return

        val responseClass = documented.response.java
        val models = ModelConverters.getInstance().read(responseClass)
        val responseSchema = models.values.firstOrNull() ?: return

        val apiResponse = operation.responses["200"] ?: SwaggerApiResponse().description("OK")

        // ApiResponse.Success<T> 래퍼 스키마
        val wrappedSchema = ObjectSchema().apply {
            properties = mapOf(
                "body" to responseSchema,
                "status" to IntegerSchema().apply {
                    format = "int32"
                    example = 200
                    _default(200)
                }
            )
            required = listOf("body", "status")
        }

        val content = Content()
        val mediaType = MediaType().apply {
            schema = wrappedSchema
        }

        content.addMediaType(APPLICATION_JSON_VALUE, mediaType)
        apiResponse.content = content
        operation.responses["200"] = apiResponse
    }
}