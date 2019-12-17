package temno.wine.security

import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint::class.java)

@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(httpServletRequest: HttpServletRequest,
                          httpServletResponse: HttpServletResponse,
                          e: AuthenticationException) {
        logger.error("Responding with unauthorized error. Message - {}", e.message)
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.message)
    }
}
