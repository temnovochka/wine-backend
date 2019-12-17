package temno.wine.security

import io.jsonwebtoken.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import temno.wine.model.User
import java.lang.Long.parseLong
import java.util.*

private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

@Component
class JwtTokenProvider {

    @Value("\${app.jwtSecret}")
    private lateinit var jwtSecret: String

    @Value("\${app.jwtExpirationInMs}")
    private var jwtExpirationInMs: Int = 0

    fun generateToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as User
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationInMs)
        return Jwts.builder()
                .setSubject(userPrincipal.id.toString())
                .addClaims(mapOf(
                        "login" to userPrincipal.login,
                        "role" to userPrincipal.role.name
                ))
                .setIssuedAt(Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact()
    }

    fun getUserIdFromJWT(token: String): Long {
        val claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .body
        return parseLong(claims.subject)
    }

    fun validateToken(authToken: String): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken)
            return true
        } catch (ex: SignatureException) {
            logger.error("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty.")
        }

        return false
    }
}
