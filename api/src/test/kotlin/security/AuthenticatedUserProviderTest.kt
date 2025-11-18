package security

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class AuthenticatedUserProviderTest {

    private lateinit var provider: AuthenticatedUserProvider

    @BeforeEach
    fun setup() {
        provider = AuthenticatedUserProvider()
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `getCurrentUserId should return sub claim from JWT`() {
        val jwt =
            Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "user123")
                .build()

        val authentication = JwtAuthenticationToken(jwt)
        SecurityContextHolder.getContext().authentication = authentication

        val userId = provider.getCurrentUserId()

        assertEquals("user123", userId)
    }

    @Test
    fun `getCurrentUserId should return subject when sub claim is null`() {
        val jwt =
            Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .subject("user456")
                .build()

        val authentication = JwtAuthenticationToken(jwt)
        SecurityContextHolder.getContext().authentication = authentication

        val userId = provider.getCurrentUserId()

        assertEquals("user456", userId)
    }

    @Test
    fun `getCurrentUserId should throw when no authentication`() {
        SecurityContextHolder.clearContext()

        assertThrows(IllegalStateException::class.java) {
            provider.getCurrentUserId()
        }
    }
}
