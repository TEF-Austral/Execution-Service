package utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.LinkedBlockingQueue

class WebSocketInputReceiverTest {

    private lateinit var session: WebSocketSession
    private lateinit var inputQueue: LinkedBlockingQueue<String>
    private lateinit var objectMapper: ObjectMapper
    private lateinit var receiver: WebSocketInputReceiver

    @BeforeEach
    fun setup() {
        session = mock()
        inputQueue = LinkedBlockingQueue()
        objectMapper = ObjectMapper()
        receiver = WebSocketInputReceiver(session, inputQueue, objectMapper)
        whenever(session.isOpen).thenReturn(true)
    }

    @Test
    fun `input should send request message`() {
        inputQueue.offer("response")

        receiver.input("Enter value")

        verify(session).sendMessage(any<TextMessage>())
    }

    @Test
    fun `input should return value from queue`() {
        inputQueue.offer("test value")

        val result = receiver.input("prompt")

        assertEquals("test value", result)
    }

    @Test
    fun `input should use default prompt when name is null`() {
        inputQueue.offer("value")

        receiver.input(null)

        verify(session).sendMessage(any<TextMessage>())
    }
}
