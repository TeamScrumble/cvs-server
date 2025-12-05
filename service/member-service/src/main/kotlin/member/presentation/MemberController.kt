package member.presentation

import ApiResponse
import jakarta.validation.Valid
import member.MemberApi
import member.member.NicknameExistsApi
import member.member.UpdateNicknameApi
import member.application.MemberService
import member.member.MemberAddApi
import member.member.MemberGetApi
import org.springframework.web.bind.annotation.*
import passport.Passport
import security.passport.RequestPassport

@RestController
class MemberController(
    private val memberService: MemberService
) : MemberApi {

    @PostMapping(MemberAddApi.PATH)
    override suspend fun add(
        @RequestBody @Valid request: MemberAddApi.Request
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

    @PostMapping(UpdateNicknameApi.PATH)
    override suspend fun updateNickname(
        @RequestBody @Valid request: UpdateNicknameApi.Request,
        @RequestPassport passport: Passport
    ): ApiResponse<UpdateNicknameApi.Response> {
        memberService.updateNickname(passport, request.nickname)
        val response = UpdateNicknameApi.Response(passport.memberId, request.nickname)

        return ApiResponse.Success(response)
    }

    @PostMapping(NicknameExistsApi.PATH)
    override suspend fun nicknameExists(
        @RequestBody @Valid request: NicknameExistsApi.Request
    ): ApiResponse<NicknameExistsApi.Response> {
        val exists = memberService.nicknameExists(request.nickname)
        val response = NicknameExistsApi.Response(exists)

        return ApiResponse.Success(response)
    }
}
