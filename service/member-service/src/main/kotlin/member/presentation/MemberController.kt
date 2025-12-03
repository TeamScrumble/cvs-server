package member.presentation

import ApiResponse
import member.MemberAddApi
import member.MemberApi
import member.MemberGetApi
import member.UpdateNicknameApi
import member.application.MemberService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import passport.Passport
import security.passport.RequestPassport

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
        val response = memberService.findById(memberId)

        return ApiResponse.Success(response)
    }

    @GetMapping(UpdateNicknameApi.PATH + "/{memberId}")
    override suspend fun updateNickname(
        @RequestBody request: UpdateNicknameApi.Request,
        @RequestPassport passport: Passport
    ): ApiResponse<UpdateNicknameApi.Response> {
        memberService.updateNickname(passport, request.nickname)
        val response = UpdateNicknameApi.Response(passport.memberId, request.nickname)

        return ApiResponse.Success(response)
    }
}
