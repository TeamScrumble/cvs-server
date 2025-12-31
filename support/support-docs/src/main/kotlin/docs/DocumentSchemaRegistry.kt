package docs

import io.swagger.v3.oas.models.media.Schema
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class DocumentSchemaRegistry {
    private val schemas = ConcurrentHashMap<String, Schema<*>>()

    fun putIfAbsent(name: String, schema: Schema<*>) {
        schemas.putIfAbsent(name, schema)
    }

    fun snapshot(): Map<String, Schema<*>> = schemas.toMap()
}