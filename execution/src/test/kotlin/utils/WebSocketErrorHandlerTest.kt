package utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

class WebSocketErrorHandlerTest {

    private lateinit var handler: WebSocketErrorHandler
    private lateinit var session: WebSocketSession
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        session = mock(WebSocketSession::class.java)
        objectMapper = ObjectMapper()
        handler = WebSocketErrorHandler(session, objectMapper)
    }

    @Test
    fun testReportError() {
        val errorMessage = "Test error"

        `when`(session.isOpen).thenReturn(true)

        handler.reportError(errorMessage)

        verify(session).sendMessage(org.mockito.kotlin.any<TextMessage>())
    }

    @Test
    fun testReportErrorWithNullMessage() {
        `when`(session.isOpen).thenReturn(true)

        handler.reportError(null)

        verify(
            session,
            org.mockito.Mockito.never(),
        ).sendMessage(org.mockito.kotlin.any<TextMessage>())
    }

    @Test
    fun testReportErrorWhenSessionClosed() {
        val errorMessage = "Test error"

        `when`(session.isOpen).thenReturn(false)

        handler.reportError(errorMessage)

        verify(
            session,
            org.mockito.Mockito.never(),
        ).sendMessage(org.mockito.kotlin.any<TextMessage>())
    }
}
