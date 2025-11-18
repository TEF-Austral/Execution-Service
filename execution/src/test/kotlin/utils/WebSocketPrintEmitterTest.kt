package utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import result.InterpreterResult
import type.CommonTypes
import variable.Variable

class WebSocketPrintEmitterTest {

    private lateinit var session: WebSocketSession
    private lateinit var objectMapper: ObjectMapper
    private lateinit var emitter: WebSocketPrintEmitter

    @BeforeEach
    fun setup() {
        session = mock()
        objectMapper = ObjectMapper()
        emitter = WebSocketPrintEmitter(session, objectMapper)
        whenever(session.isOpen).thenReturn(true)
    }

    @Test
    fun `emit should send message when session is open`() {
        val result = InterpreterResult(true, "success", Variable(CommonTypes.STRING, "test"))

        emitter.emit(result)

        verify(session).sendMessage(any<TextMessage>())
    }

    @Test
    fun `stringEmit should send message when session is open`() {
        emitter.stringEmit("test message")

        verify(session).sendMessage(any<TextMessage>())
    }

    @Test
    fun `emit should not throw when session is closed`() {
        whenever(session.isOpen).thenReturn(false)
        val result = InterpreterResult(true, "success", Variable(CommonTypes.STRING, "test"))

        emitter.emit(result)
    }

    @Test
    fun `stringEmit should not throw when session is closed`() {
        whenever(session.isOpen).thenReturn(false)

        emitter.stringEmit("test")
    }
}
