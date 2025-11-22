data class ApiResponse<T>(
    val body: T,
    val status: Int = 200,
)