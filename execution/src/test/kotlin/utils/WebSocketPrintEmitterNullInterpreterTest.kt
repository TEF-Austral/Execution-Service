package utils

import com.fasterxml.jackson.databind.ObjectMapper
import dtos.WebSocketMessage
import dtos.WebSocketMessageType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import result.InterpreterResult

class WebSocketPrintEmitterNullInterpreterTest {

    @Test
    fun `emit should send 'null' string when interpreter is null`() {
        val mockSession = mock(WebSocketSession::class.java)
        val objectMapper = ObjectMapper()
        `when`(mockSession.isOpen).thenReturn(true)

        val emitter = WebSocketPrintEmitter(mockSession, objectMapper)
        val result = InterpreterResult(true, "msg", null)

        emitter.emit(result)

        val captor = ArgumentCaptor.forClass(TextMessage::class.java)
        verify(mockSession).sendMessage(captor.capture())

        val sentMessage = objectMapper.readValue(captor.value.payload, WebSocketMessage::class.java)
        assertEquals(WebSocketMessageType.Output, sentMessage.type)
        assertEquals("null", sentMessage.value)
    }
}
