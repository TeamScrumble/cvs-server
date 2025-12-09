package member.application

import db.transactional.Transactional
import error.errorcode.MemberErrorCode
import error.exception.BusinessException
import member.domain.member.Member
import member.domain.member.MemberRepository
import member.domain.member.MemberRole
import member.member.MemberGetApi
import org.springframework.stereotype.Service
import passport.Passport

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

    suspend fun findById(id: Long): MemberGetApi.Response = transactional {
        val member = memberRepository.findById(id)
            ?: throw BusinessException(MemberErrorCode.M_001)
        MemberGetApi.Response(
            memberId = member.id,
            email = member.email,
            roles = member.roles.map { it.name }.toSet(),
            nickname = member.nickname
        )
    }

    suspend fun updateNickname(
        passport: Passport,
        nickname: String
    ) = transactional {
        if (memberRepository.existsByNickname(nickname)) {
            throw BusinessException(MemberErrorCode.M_002)
        }

        val member = memberRepository.findById(passport.memberId)
            ?: throw BusinessException(MemberErrorCode.M_001)

        val updated = member.copy(nickname = nickname)
        memberRepository.save(updated)
    }

    suspend fun nicknameExists(nickname: String): Boolean {
        return memberRepository.existsByNickname(nickname)
    }
}