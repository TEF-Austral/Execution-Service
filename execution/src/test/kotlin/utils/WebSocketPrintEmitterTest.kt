package utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import result.InterpreterResult
import type.CommonTypes
import variable.Variable

class WebSocketPrintEmitterTest {

    private lateinit var emitter: WebSocketPrintEmitter
    private lateinit var session: WebSocketSession
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        session = mock(WebSocketSession::class.java)
        objectMapper = ObjectMapper()
        emitter = WebSocketPrintEmitter(session, objectMapper)
    }

    @Test
    fun testEmit() {
        val value = "5"
        val variable = Variable(CommonTypes.STRING, value)
        val result = InterpreterResult(true, "Success", variable)

        `when`(session.isOpen).thenReturn(true)

        emitter.emit(result)

        verify(session).sendMessage(org.mockito.kotlin.any<TextMessage>())
    }

    @Test
    fun testStringEmit() {
        val value = "Hello World"

        `when`(session.isOpen).thenReturn(true)

        emitter.stringEmit(value)

        verify(session).sendMessage(org.mockito.kotlin.any<TextMessage>())
    }

    @Test
    fun testEmitWhenSessionClosed() {
        val value = "5"
        val variable = Variable(CommonTypes.STRING, value)
        val result = InterpreterResult(true, "Success", variable)

        `when`(session.isOpen).thenReturn(false)

        emitter.emit(result)

        verify(
            session,
            org.mockito.Mockito.never(),
        ).sendMessage(org.mockito.kotlin.any<TextMessage>())
    }

    @Test
    fun testStringEmitWhenSessionClosed() {
        val value = "Hello World"

        `when`(session.isOpen).thenReturn(false)

        emitter.stringEmit(value)

        verify(
            session,
            org.mockito.Mockito.never(),
        ).sendMessage(org.mockito.kotlin.any<TextMessage>())
    }
}
