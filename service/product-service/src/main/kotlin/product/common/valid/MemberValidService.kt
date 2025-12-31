package product.common.valid

import extension.getOrThrow
import member.MemberApi
import org.springframework.stereotype.Service
import passport.Passport

@Service
class MemberValidService(
    private val memberApi: MemberApi
) {
    suspend fun validateMember(passport: Passport) {
        memberApi.get(passport.memberId).getOrThrow()
    }
}