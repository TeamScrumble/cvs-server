package member.member.field

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@NotBlank
@Pattern(regexp = "\"^[A-Za-z0-9가-힣_]{2,15}\$\"")
annotation class Nickname
