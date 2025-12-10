package member.domain.member

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MemberRepository : CoroutineCrudRepository<Member, Long> {
    suspend fun existsByNickname(nickname: String): Boolean
}
