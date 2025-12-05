package auth.emailAuth.field

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@Target(allowedTargets = [AnnotationTarget.FIELD])
@Retention(AnnotationRetention.RUNTIME)
@NotBlank
@Pattern(regexp = "\"^[0-9]{6}\$\"")
annotation class VerificationCode()
