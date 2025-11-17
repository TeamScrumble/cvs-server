package security.password

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class PasswordEncoder {

    private val bCryptPasswordEncoder = BCryptPasswordEncoder()

    fun encode(raw: String): String {
        return bCryptPasswordEncoder.encode(raw)
    }

    fun matches(raw: String, encoded: String): Boolean {
        return bCryptPasswordEncoder.matches(raw, encoded)
    }
}
