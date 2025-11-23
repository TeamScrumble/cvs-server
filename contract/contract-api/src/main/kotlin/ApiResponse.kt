import error.ErrorResponse

data class ApiResponse<T> (
    val body: T? = null,
    val error: ErrorResponse? = null,
    val status: Int,
) {
    val success: Boolean
        get() = status in 200 .. 299

    companion object {
        fun <T> of(body: T, status: Int = 200): ApiResponse<T> {
            return ApiResponse(body, null, status)
        }

        fun <T> error(errorResponse: ErrorResponse, status: Int = 500): ApiResponse<T> {
            return ApiResponse(null, errorResponse, status)
        }
    }
}