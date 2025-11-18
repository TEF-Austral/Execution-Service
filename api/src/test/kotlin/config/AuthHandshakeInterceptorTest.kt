package api.config

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.web.socket.WebSocketHandler
import java.net.URI

class AuthHandshakeInterceptorTest {

    private lateinit var jwtDecoder: JwtDecoder
    private lateinit var interceptor: AuthHandshakeInterceptor

    @BeforeEach
    fun setup() {
        jwtDecoder = mockk(relaxed = true)
        interceptor = AuthHandshakeInterceptor(jwtDecoder)
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `beforeHandshake should return true with valid token`() {
        val token = "valid-jwt-token"
        val uri = URI.create("ws://localhost/ws/execute-interactive?token=$token")
        val request = mockk<ServerHttpRequest>(relaxed = true)
        val response = mockk<ServerHttpResponse>(relaxed = true)
        val wsHandler = mockk<WebSocketHandler>(relaxed = true)
        val attributes = mutableMapOf<String, Any>()
        val jwt = mockk<Jwt>(relaxed = true)

        every { request.uri } returns uri
        every { jwtDecoder.decode(token) } returns jwt

        val result = interceptor.beforeHandshake(request, response, wsHandler, attributes)

        assertTrue(result)
        verify { jwtDecoder.decode(token) }
        assertNotNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `beforeHandshake should return false when token is missing`() {
        val uri = URI.create("ws://localhost/ws/execute-interactive")
        val request = mockk<ServerHttpRequest>(relaxed = true)
        val response = mockk<ServerHttpResponse>(relaxed = true)
        val wsHandler = mockk<WebSocketHandler>(relaxed = true)
        val attributes = mutableMapOf<String, Any>()

        every { request.uri } returns uri

        val result = interceptor.beforeHandshake(request, response, wsHandler, attributes)

        assertFalse(result)
        verify(exactly = 0) { jwtDecoder.decode(any()) }
    }

    @Test
    fun `beforeHandshake should return false when token is blank`() {
        val uri = URI.create("ws://localhost/ws/execute-interactive?token=")
        val request = mockk<ServerHttpRequest>(relaxed = true)
        val response = mockk<ServerHttpResponse>(relaxed = true)
        val wsHandler = mockk<WebSocketHandler>(relaxed = true)
        val attributes = mutableMapOf<String, Any>()

        every { request.uri } returns uri

        val result = interceptor.beforeHandshake(request, response, wsHandler, attributes)

        assertFalse(result)
        verify(exactly = 0) { jwtDecoder.decode(any()) }
    }

    @Test
    fun `beforeHandshake should return false when token is invalid`() {
        val token = "invalid-token"
        val uri = URI.create("ws://localhost/ws/execute-interactive?token=$token")
        val request = mockk<ServerHttpRequest>(relaxed = true)
        val response = mockk<ServerHttpResponse>(relaxed = true)
        val wsHandler = mockk<WebSocketHandler>(relaxed = true)
        val attributes = mutableMapOf<String, Any>()

        every { request.uri } returns uri
        every { jwtDecoder.decode(token) } throws RuntimeException("Invalid JWT")

        val result = interceptor.beforeHandshake(request, response, wsHandler, attributes)

        assertFalse(result)
        verify { jwtDecoder.decode(token) }
    }

    @Test
    fun `afterHandshake should clear security context`() {
        val request = mockk<ServerHttpRequest>(relaxed = true)
        val response = mockk<ServerHttpResponse>(relaxed = true)
        val wsHandler = mockk<WebSocketHandler>(relaxed = true)

        SecurityContextHolder.getContext().authentication = mockk(relaxed = true)
        assertNotNull(SecurityContextHolder.getContext().authentication)

        interceptor.afterHandshake(request, response, wsHandler, null)

        assertEquals(null, SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `afterHandshake should clear security context even with exception`() {
        val request = mockk<ServerHttpRequest>(relaxed = true)
        val response = mockk<ServerHttpResponse>(relaxed = true)
        val wsHandler = mockk<WebSocketHandler>(relaxed = true)
        val exception = RuntimeException("Test exception")

        SecurityContextHolder.getContext().authentication = mockk(relaxed = true)

        interceptor.afterHandshake(request, response, wsHandler, exception)

        assertEquals(null, SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `beforeHandshake should handle multiple query parameters`() {
        val token = "valid-token"
        val uri = URI.create("ws://localhost/ws/execute-interactive?token=$token&otherparam=value")
        val request = mockk<ServerHttpRequest>(relaxed = true)
        val response = mockk<ServerHttpResponse>(relaxed = true)
        val wsHandler = mockk<WebSocketHandler>(relaxed = true)
        val attributes = mutableMapOf<String, Any>()
        val jwt = mockk<Jwt>(relaxed = true)

        every { request.uri } returns uri
        every { jwtDecoder.decode(token) } returns jwt

        val result = interceptor.beforeHandshake(request, response, wsHandler, attributes)

        assertTrue(result)
        verify { jwtDecoder.decode(token) }
    }
}
