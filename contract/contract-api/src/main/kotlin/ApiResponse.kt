data class ApiResponse<T>(
    val body: T,
    val status: Int,
) {
    companion object
}

fun <T> ApiResponse.Companion.of(
    body :T,
    status: Int = 200
) : ApiResponse<T> {
    return ApiResponse(body, status)
}

fun <T> ApiResponse.Companion.error(
    body :T,
    status: Int = 500
) : ApiResponse<T> {
    return ApiResponse(body, status)
}
