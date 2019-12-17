package temno.wine.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import temno.wine.exception.ResourceNotFoundException
import temno.wine.repository.UserRepository
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter : OncePerRequestFilter() {

    @Autowired
    private lateinit var tokenProvider: JwtTokenProvider

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val jwt = request.getHeader("Authorization")
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            val userId = tokenProvider.getUserIdFromJWT(jwt)
            val user = userRepository.findById(userId).orElseThrow { ResourceNotFoundException("User", "id", userId) }
            SecurityContextHolder.getContext().authentication = user
        }
        filterChain.doFilter(request, response)
    }
}
