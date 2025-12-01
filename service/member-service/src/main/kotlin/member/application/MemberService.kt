package member.application

import db.transactional.Transactional
import member.domain.member.Member
import member.domain.member.MemberRepository
import member.domain.member.MemberRole
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val transactional: Transactional,
    private val memberRepository: MemberRepository
) {

    private val defaultRoles = setOf(MemberRole.ROLE_USER)

    private val nicknamePrefixes = listOf(
        "청초한", "유쾌한", "따뜻한", "감성적인",
        "차분한", "도도한", "용감한", "섬세한"
    )

    private val nicknameSuffixes = listOf(
        "고양이", "강아지", "여우", "수달",
        "토끼", "부엉이", "고슴도치", "사막여우"
    )

    suspend fun add(email: String): Long = transactional {
        val member = Member(
            email = email,
            roles = defaultRoles,
            nickname = generateNickname()
        )

        val saved = memberRepository.save(member)

        saved.id
    }

    private fun generateNickname(): String {
        val prefix = nicknamePrefixes.random()
        val suffix = nicknameSuffixes.random()
        return "$prefix $suffix"
    }
}