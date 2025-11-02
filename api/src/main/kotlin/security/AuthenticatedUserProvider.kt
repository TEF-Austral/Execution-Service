package security

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class AuthenticatedUserProvider {

    fun getCurrentUserId(): String {
        val authentication: Authentication =
            SecurityContextHolder.getContext().authentication
                ?: throw IllegalStateException("No authentication found")

        val jwt =
            authentication.principal as? Jwt
                ?: throw IllegalStateException("Invalid authentication type")

        return jwt.getClaim<String>("sub")
            ?: jwt.subject
            ?: throw IllegalStateException("User ID not found in token")
    }
}
