package error

data class ErrorResponse(
    val code: String,
    val description: String,
    val from: String
)
