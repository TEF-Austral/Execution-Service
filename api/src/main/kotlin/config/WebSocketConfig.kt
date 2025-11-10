package api.config

import api.handlers.InteractiveExecutionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val interactiveExecutionHandler: InteractiveExecutionHandler,
    private val authInterceptor: AuthHandshakeInterceptor,
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(interactiveExecutionHandler, "/ws/execute-interactive")
            .addInterceptors(authInterceptor)
            .setAllowedOrigins("*")
    }
}
