package member

import io.swagger.v3.oas.annotations.tags.Tag
import member.member.MemberAddApi
import member.member.MemberGetApi
import member.member.NicknameExistsApi
import member.member.UpdateNicknameApi

@Tag(name = "Member", description = "회원 API")
interface MemberApi : MemberAddApi, MemberGetApi, UpdateNicknameApi, NicknameExistsApi