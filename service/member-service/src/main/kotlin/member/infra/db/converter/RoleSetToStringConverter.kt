package member.infra.db.converter

import member.domain.member.MemberRole
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component

@Component
@WritingConverter
class RoleSetToStringConverter : Converter<Set<MemberRole>, String> {
    override fun convert(source: Set<MemberRole>): String =
        source.joinToString(",") { it.name }
}