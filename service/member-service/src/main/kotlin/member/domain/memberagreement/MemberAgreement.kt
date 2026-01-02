package member.domain.memberagreement

import db.base.LongIdEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("member_agreement")
data class MemberAgreement(
    @Id
    @Column("member_agreement_id")
    override val id: Long = 0,

    @Column("agreed")
    val agreed: Boolean,

    @Column("agreed_at")
    val agreedAt: LocalDateTime,

    @Column("member_id")
    val memberId: Long,

    @Column("agreement_id")
    val agreementId: Long
) : LongIdEntity()
