package utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

class WebSocketErrorHandlerTest {

    private lateinit var session: WebSocketSession
    private lateinit var objectMapper: ObjectMapper
    private lateinit var handler: WebSocketErrorHandler

    @BeforeEach
    fun setup() {
        session = mock()
        objectMapper = ObjectMapper()
        handler = WebSocketErrorHandler(session, objectMapper)
    }

    @Test
    fun `reportError should send message when session is open`() {
        whenever(session.isOpen).thenReturn(true)

        handler.reportError("error message")

        verify(session).sendMessage(any<TextMessage>())
    }

    @Test
    fun `reportError should not send when session is closed`() {
        whenever(session.isOpen).thenReturn(false)

        handler.reportError("error message")

        verify(session, never()).sendMessage(any<TextMessage>())
    }

    @Test
    fun `reportError should not send when message is null`() {
        whenever(session.isOpen).thenReturn(true)

        handler.reportError(null)

        verify(session, never()).sendMessage(any<TextMessage>())
    }
}
