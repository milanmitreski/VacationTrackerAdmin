package rbt.vacationtracker.utils

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

object Encoder {
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

    fun check(
        original: String,
        hashed: String,
    ): Boolean = passwordEncoder.matches(original, hashed)

    fun encode(string: String): String = passwordEncoder.encode(string)
}
