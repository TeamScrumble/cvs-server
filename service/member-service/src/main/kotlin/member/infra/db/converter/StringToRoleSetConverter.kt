package member.infra.db.converter

import member.domain.member.MemberRole
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.stereotype.Component

@Component
@ReadingConverter
class StringToRoleSetConverter : Converter<String, Set<MemberRole>> {
    override fun convert(source: String): Set<MemberRole> =
        if (source.isEmpty()) emptySet() else source.split(",").map { MemberRole.valueOf(it) }.toSet()
}