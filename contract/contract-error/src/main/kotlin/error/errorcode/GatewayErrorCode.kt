package error.errorcode

enum class GatewayErrorCode(
    override val code: String,
    override val description: String
) : ErrorCode {
    G_001("G_001", "잘못된 요청입니다."),
    G_002("G_002", "요청 시간이 초과하였습니다.")
}