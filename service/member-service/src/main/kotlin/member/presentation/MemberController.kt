package member.presentation

import ApiResponse
import member.MemberAddApi
import member.MemberApi
import member.MemberGetApi
import of
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import passport.Passport
import security.passport.RequestPassport

@RestController
class MemberController(
) : MemberApi {

    @PostMapping(MemberAddApi.PATH)
    override suspend fun add(
        @RequestBody request: MemberAddApi.Request
    ): ApiResponse<MemberAddApi.Response> {
        return ApiResponse.of(MemberAddApi.Response(1L))
    }

    @GetMapping(MemberGetApi.PATH + "/{memberId}")
    override suspend fun get(
        @PathVariable memberId: Long
    ): ApiResponse<MemberGetApi.Response> {
        return ApiResponse.of(MemberGetApi.Response(1L, setOf("user"), "nickname"))
    }
}
