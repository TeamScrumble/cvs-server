package member

import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Member", description = "회원 API")
interface MemberApi : MemberAddApi, MemberGetApi, UpdateNicknameApi, NicknameExistsApi, MemberListApi