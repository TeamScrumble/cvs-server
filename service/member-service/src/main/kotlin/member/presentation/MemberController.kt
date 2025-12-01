package member.presentation

import ApiResponse
import member.MemberAddApi
import member.MemberApi
import member.MemberGetApi
import member.application.MemberService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberController(
    private val memberService: MemberService
) : MemberApi {

    @PostMapping(MemberAddApi.PATH)
    override suspend fun add(
        @RequestBody request: MemberAddApi.Request
    ): ApiResponse<MemberAddApi.Response> {
        val memberId = memberService.add(request.email)
        val response = MemberAddApi.Response(memberId)

        return ApiResponse.Success(response)
    }

    @GetMapping(MemberGetApi.PATH + "/{memberId}")
    override suspend fun get(
        @PathVariable memberId: Long
    ): ApiResponse<MemberGetApi.Response> {
        return ApiResponse.Success(MemberGetApi.Response(1L, "xx@gmail.com", setOf("user"), "nickname"))
    }
}
