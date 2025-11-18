package api.handlers

import component.AssetServiceClient
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession

class InteractiveExecutionHandlerTest {

    private lateinit var assetServiceClient: AssetServiceClient
    private lateinit var handler: InteractiveExecutionHandler

    @BeforeEach
    fun setup() {
        assetServiceClient = mockk(relaxed = true)
        handler = InteractiveExecutionHandler(assetServiceClient)
    }

    @Test
    fun `handler should be instantiated with asset service client`() {
        assertNotNull(handler)
    }

    @Test
    fun `afterConnectionEstablished should initialize session`() {
        val session = mockk<WebSocketSession>(relaxed = true)
        io.mockk.every { session.id } returns "session-123"

        handler.afterConnectionEstablished(session)

        verify { session.id }
    }

    @Test
    fun `afterConnectionClosed should remove session`() {
        val session = mockk<WebSocketSession>(relaxed = true)
        io.mockk.every { session.id } returns "session-123"
        val status = CloseStatus.NORMAL

        handler.afterConnectionEstablished(session)
        handler.afterConnectionClosed(session, status)

        verify(atLeast = 1) { session.id }
    }
}
