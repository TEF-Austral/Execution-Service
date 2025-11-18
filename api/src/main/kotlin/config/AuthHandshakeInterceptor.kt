package api.config

import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import org.springframework.web.util.UriComponentsBuilder
import java.lang.Exception

@Component
class AuthHandshakeInterceptor(
    private val jwtDecoder: JwtDecoder,
) : HandshakeInterceptor {

    private val log = LoggerFactory.getLogger(AuthHandshakeInterceptor::class.java)

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>,
    ): Boolean {
        try {
            val queryParams = UriComponentsBuilder.fromUri(request.uri).build().queryParams
            val token = queryParams.getFirst("token")

            if (token.isNullOrBlank()) {
                log.warn("Rejecting connection. Missing M2M token.")
                return false
            }
            val jwt = jwtDecoder.decode(token)
            val converter = JwtAuthenticationConverter()
            val authentication = converter.convert(jwt)
            SecurityContextHolder.getContext().authentication = authentication

            return true
        } catch (e: Exception) {
            log.warn("Error processing M2M token: ${e.message}", e)
            return false
        }
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?,
    ) {
        SecurityContextHolder.clearContext()
    }
}
