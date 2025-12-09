package auth.emailAuth.field

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(allowedTargets = [AnnotationTarget.FIELD])
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [Password.PasswordValidator::class])
annotation class Password(
    val message: String = "비밀번호 형식이 올바르지 않습니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
) {

    class PasswordValidator : ConstraintValidator<Password, String> {
        override fun isValid(password: String?, context: ConstraintValidatorContext): Boolean {
            if (password.isNullOrBlank()) return false

            if (password.length !in 8..32) return false

            val hasLetter = password.any { it.isLetter() }
            val hasDigit = password.any { it.isDigit() }
            val hasSpecial = password.any { it in "!@#$%^&*()_+-={}[]|:;\"'<>,.?/`~" }

            val typeCount = listOf(hasLetter, hasDigit, hasSpecial).count { it }

            return typeCount >= 2
        }
    }
}