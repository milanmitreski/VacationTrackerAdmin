package rbt.vacationtracker.security

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.stereotype.Service
import rbt.vacationtracker.error.AuthenticationError
import rbt.vacationtracker.error.BadCredentialsError
import rbt.vacationtracker.utils.Encoder

@Service
class AuthenticationService(
    @Value("\${auth.token.header-name}")
    private val authTokenHeaderName: String,
    @Value("\${auth.token.value}")
    private val authTokenValue: String,
) {
    fun getAuthentication(request: HttpServletRequest): Result<Authentication, AuthenticationError> {
        val apiKey = request.getHeader(authTokenHeaderName)
        return if (apiKey == null || !Encoder.check(apiKey, authTokenValue)) {
            Err(BadCredentialsError("Invalid token", "X-API-KEY"))
        } else {
            Ok(ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES))
        }
    }
}
