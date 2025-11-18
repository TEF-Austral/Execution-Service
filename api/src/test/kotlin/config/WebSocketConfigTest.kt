package config

import api.config.AuthHandshakeInterceptor
import api.config.WebSocketConfig
import api.handlers.InteractiveExecutionHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

class WebSocketConfigTest {

    private lateinit var interactiveExecutionHandler: InteractiveExecutionHandler
    private lateinit var authInterceptor: AuthHandshakeInterceptor
    private lateinit var webSocketConfig: WebSocketConfig

    @BeforeEach
    fun setup() {
        interactiveExecutionHandler = mockk(relaxed = true)
        authInterceptor = mockk(relaxed = true)
        webSocketConfig = WebSocketConfig(interactiveExecutionHandler, authInterceptor)
    }

    @Test
    fun `registerWebSocketHandlers should register handler with correct path`() {
        val registry = mockk<WebSocketHandlerRegistry>(relaxed = true)
        val registration = mockk<WebSocketHandlerRegistration>(relaxed = true)

        every {
            registry.addHandler(interactiveExecutionHandler, "/ws/execute-interactive")
        } returns registration
        every { registration.addInterceptors(authInterceptor) } returns registration
        every { registration.setAllowedOrigins("*") } returns registration

        webSocketConfig.registerWebSocketHandlers(registry)

        verify { registry.addHandler(interactiveExecutionHandler, "/ws/execute-interactive") }
        verify { registration.addInterceptors(authInterceptor) }
        verify { registration.setAllowedOrigins("*") }
    }

    @Test
    fun `WebSocketConfig should be instantiated with dependencies`() {
        assertNotNull(webSocketConfig)
    }
}
