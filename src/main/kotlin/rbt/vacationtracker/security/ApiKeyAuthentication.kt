package rbt.vacationtracker.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class ApiKeyAuthentication constructor(
    private val apiKey: String,
    noAuthorities: List<GrantedAuthority>,
) : AbstractAuthenticationToken(noAuthorities) {
    init {
        isAuthenticated = true
    }

    override fun getCredentials(): Any? = null

    override fun getPrincipal(): Any = apiKey
}
