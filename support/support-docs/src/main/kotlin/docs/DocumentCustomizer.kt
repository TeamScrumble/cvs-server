package docs

import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.models.OpenAPI
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
class DocumentCustomizer(
    private val registry: DocumentSchemaRegistry,
) : OperationCustomizer {

    override fun customize(operation: Operation, handlerMethod: HandlerMethod): Operation {
        val documented = handlerMethod.getMethodAnnotation(Documented::class.java)
            ?: return operation

        operation.summary = documented.summary
        operation.description = documented.description

        customizeRequest(operation, documented)
        customizePassport(operation)
        customizeResponse(operation, documented)

        return operation
    }

    private fun customizeRequest(operation: Operation, documented: Documented) {
        if (documented.request == Nothing::class) return

        val requestRef = registerSchemaAndGetRef(documented.request.java)

        val requestBody = operation.requestBody ?: RequestBody()
        val content = requestBody.content ?: io.swagger.v3.oas.models.media.Content()
        val mediaType = content[APPLICATION_JSON_VALUE] ?: io.swagger.v3.oas.models.media.MediaType()

        mediaType.schema = Schema<Any>().apply { `$ref` = requestRef }

        content.addMediaType(APPLICATION_JSON_VALUE, mediaType)
        requestBody.content = content
        operation.requestBody = requestBody
    }

    private fun customizePassport(operation: Operation) {
        val hadPassportParam = operation.parameters
            ?.any { it.name?.contains("passport", ignoreCase = true) == true }
            ?: false

        operation.parameters = operation.parameters
            ?.filterNot { it.name?.contains("passport", ignoreCase = true) == true }
            ?.takeIf { it.isNotEmpty() }

        if (!hadPassportParam) return

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

    private fun customizeResponse(operation: Operation, documented: Documented) {
        if (documented.response == Nothing::class) return

        val responseRef = registerSchemaAndGetRef(documented.response.java)
        val apiResponse = operation.responses["200"] ?: SwaggerApiResponse().description("OK")

        val wrappedSchema = ObjectSchema().apply {
            properties = mapOf(
                "body" to Schema<Any>().apply { `$ref` = responseRef },
                "status" to IntegerSchema().apply {
                    format = "int32"
                    example = 200
                    _default(200)
                }
            )
            required = listOf("body", "status")
        }

        val content = io.swagger.v3.oas.models.media.Content()
        val mediaType = io.swagger.v3.oas.models.media.MediaType().apply { schema = wrappedSchema }
        content.addMediaType(APPLICATION_JSON_VALUE, mediaType)

        apiResponse.content = content
        operation.responses["200"] = apiResponse
    }

    private fun registerSchemaAndGetRef(clazz: Class<*>): String {
        val owner = clazz.enclosingClass?.simpleName ?: clazz.simpleName
        val mainOldName = clazz.simpleName
        val mainNewName = "$owner-$mainOldName"

        // 이미 넣어둔 적 있으면 ref만 반환
        if (registry.snapshot().containsKey(mainNewName)) {
            return "#/components/schemas/$mainNewName"
        }

        val resolved = ModelConverters.getInstance().readAllAsResolvedSchema(clazz)
        val referenced = resolved.referencedSchemas ?: emptyMap()

        val renameMap = linkedMapOf<String, String>().apply {
            put(mainOldName, mainNewName)
            referenced.keys.forEach { old -> put(old, "$owner-$old") }
        }

        val mainSchema = resolved.schema ?: return "#/components/schemas/$mainNewName"
        rewriteRefsInPlace(mainSchema, renameMap)

        val rewrittenRefs = referenced.mapValues { (_, s) ->
            rewriteRefsInPlace(s, renameMap)
            s
        }

        // 레지스트리에 저장 (최종 저장은 OpenApiCustomizer가 함)
        registry.putIfAbsent(mainNewName, mainSchema)
        rewrittenRefs.forEach { (old, schema) ->
            val newName = renameMap[old] ?: return@forEach
            registry.putIfAbsent(newName, schema)
        }

        return "#/components/schemas/$mainNewName"
    }

    private fun rewriteRefsInPlace(schema: Schema<*>, renameMap: Map<String, String>) {
        fun rewriteRef(ref: String?): String? {
            if (ref.isNullOrBlank()) return ref
            val oldName = ref.substringAfterLast("/")
            val newName = renameMap[oldName] ?: return ref
            return "#/components/schemas/$newName"
        }

        schema.`$ref` = rewriteRef(schema.`$ref`)

        schema.properties?.values?.forEach { rewriteRefsInPlace(it as Schema<*>, renameMap) }

        if (schema is ArraySchema) {
            schema.items?.let { rewriteRefsInPlace(it, renameMap) }
        }

        if (schema is ComposedSchema) {
            schema.allOf?.forEach { rewriteRefsInPlace(it, renameMap) }
            schema.oneOf?.forEach { rewriteRefsInPlace(it, renameMap) }
            schema.anyOf?.forEach { rewriteRefsInPlace(it, renameMap) }
        }

        val ap = schema.additionalProperties
        if (ap is Schema<*>) rewriteRefsInPlace(ap, renameMap)
    }
}