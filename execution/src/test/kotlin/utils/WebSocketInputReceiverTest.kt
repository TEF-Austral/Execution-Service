package utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.LinkedBlockingQueue

class WebSocketInputReceiverTest {

    private lateinit var receiver: WebSocketInputReceiver
    private lateinit var session: WebSocketSession
    private lateinit var inputQueue: LinkedBlockingQueue<String>
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        session = mock(WebSocketSession::class.java)
        inputQueue = LinkedBlockingQueue()
        objectMapper = ObjectMapper()
        receiver = WebSocketInputReceiver(session, inputQueue, objectMapper)
    }

    @Test
    fun testInputSuccess() {
        val name = "testInput"
        val expectedValue = "5"

        `when`(session.isOpen).thenReturn(true)

        Thread {
            Thread.sleep(50)
            inputQueue.put(expectedValue)
        }.start()

        val result = receiver.input(name)

        assertEquals(expectedValue, result)
        verify(session).sendMessage(org.mockito.kotlin.any<TextMessage>())
    }

    @Test
    fun testInputWithNullName() {
        val expectedValue = "test"

        `when`(session.isOpen).thenReturn(true)

        Thread {
            Thread.sleep(50)
            inputQueue.put(expectedValue)
        }.start()

        val result = receiver.input(null)

        assertEquals(expectedValue, result)
        verify(session).sendMessage(org.mockito.kotlin.any<TextMessage>())
    }
}
