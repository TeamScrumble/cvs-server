package auth.domain.emailauth

import db.base.LongIdEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("email_auth")
data class EmailAuth(
    @Id
    @Column("email_auth_id")
    override val id: Long = 0,

    @Column("email")
    val email: String,

    @Column("encoded_password")
    val encodedPassword: String
) : LongIdEntity()