import error.ErrorResponse

sealed class ApiResponse<T> {
    data class Success<T>(val body: T, val status: Int = 200) : ApiResponse<T>()
    data class Error<T>(val error: ErrorResponse, val status: Int = 500) : ApiResponse<T>()
}