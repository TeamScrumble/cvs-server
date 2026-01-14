package member.domain.agreement

import db.base.LongIdEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("agreement")
data class Agreement(
    @Id
    @Column("agreement_id")
    override val id: Long = 0,

    @Column("type")
    val type: AgreementType,

    @Column("required")
    val required: Boolean,

    @Column("label")
    val label: String,

    @Column("document_url")
    val documentUrl: String,

    @Column("version")
    val version: String,

    @Column("is_active")
    val isActive: Boolean
) : LongIdEntity()
