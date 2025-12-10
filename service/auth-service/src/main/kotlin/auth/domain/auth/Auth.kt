package auth.domain.auth

import db.base.LongIdEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("auth")
data class Auth(
    @Id
    @Column("auth_id")
    override val id: Long = 0,

    @Column("provider")
    val provider: AuthProvider,

    @Column("provider_id")
    val providerId: String,

    @Column("member_id")
    val memberId: Long
) : LongIdEntity()
