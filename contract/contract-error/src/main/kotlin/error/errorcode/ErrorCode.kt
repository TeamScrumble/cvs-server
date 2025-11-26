package error.errorcode

sealed interface ErrorCode {
    val code: String
    val description: String

    companion object {
        private val errorCodeCache: Map<String, ErrorCode> = ErrorCode::class.sealedSubclasses
            .flatMap { kClass ->
                when {
                    kClass.objectInstance != null -> listOf(kClass.objectInstance as ErrorCode)
                    kClass.java.isEnum -> (kClass.java.enumConstants as Array<ErrorCode>).toList()
                    else -> emptyList()
                }
            }
            .associateBy { it.code }

        fun from(code: String): ErrorCode = errorCodeCache[code]
            ?: throw RuntimeException("Invalid Error Code")
    }
}
