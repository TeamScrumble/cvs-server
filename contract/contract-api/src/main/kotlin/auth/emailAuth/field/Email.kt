package auth.emailAuth.field

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@NotBlank
@Email
annotation class Email