package docs.config

import docs.DocumentSchemaRegistry
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DocumentOpenApiCustomizerConfig {

    @Bean
    fun documentOpenApiCustomizer(registry: DocumentSchemaRegistry): OpenApiCustomizer {
        return OpenApiCustomizer { openApi ->
            val components = openApi.components ?: io.swagger.v3.oas.models.Components().also {
                openApi.components = it
            }
            val schemas = components.schemas ?: linkedMapOf<String, io.swagger.v3.oas.models.media.Schema<*>>().also {
                components.schemas = it
            }

            registry.snapshot().forEach { (name, schema) ->
                schemas.putIfAbsent(name, schema)
            }
        }
    }
}