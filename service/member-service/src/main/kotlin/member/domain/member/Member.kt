package member.domain.member

import db.base.LongIdEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("member")
data class Member(
    @Id
    @Column("member_id")
    override val id: Long = 0,

    @Column("email")
    val email: String,

    @Column("roles")
    val roles: Set<MemberRole>,

    @Column("nickname")
    val nickname: String,

    @Column("profile_image")
    val profileImage: String,
) : LongIdEntity()
