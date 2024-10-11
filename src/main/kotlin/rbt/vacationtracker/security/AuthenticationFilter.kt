package rbt.vacationtracker.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.result.mapBoth
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean

@Component
class AuthenticationFilter(
    private val authenticationService: AuthenticationService,
) : GenericFilterBean() {
    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain,
    ) {
        val filterPath = AntPathRequestMatcher("/import/**")
        if (!filterPath.matches(request as HttpServletRequest)) {
            chain.doFilter(request, response)
        }

        authenticationService.getAuthentication(request).mapBoth(
            { authentication ->
                SecurityContextHolder.getContext().authentication = authentication
                chain.doFilter(request, response)
            },
            { error ->
                val httpResponse = response as HttpServletResponse
                httpResponse.status = HttpServletResponse.SC_UNAUTHORIZED
                httpResponse.contentType = MediaType.APPLICATION_JSON_VALUE
                val writer = httpResponse.writer
                writer.print(ObjectMapper().writeValueAsString(error))
                writer.flush()
                writer.close()
            },
        )
    }
}
