package dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class WebSocketMessageTest {

    @Test
    fun `should create WebSocketMessage with all fields`() {
        val message =
            WebSocketMessageDTO(
                type = WebSocketMessageType.Output,
                value = "test output",
                prompt = "Enter value:",
                bucketContainer = "container1",
                bucketKey = "key1",
                version = "1.0",
            )

        assertEquals(WebSocketMessageType.Output, message.type)
        assertEquals("test output", message.value)
        assertEquals("Enter value:", message.prompt)
        assertEquals("container1", message.bucketContainer)
        assertEquals("key1", message.bucketKey)
        assertEquals("1.0", message.version)
    }

    @Test
    fun `should create WebSocketMessage with minimal fields`() {
        val message =
            WebSocketMessageDTO(
                type = WebSocketMessageType.Error,
            )

        assertEquals(WebSocketMessageType.Error, message.type)
        assertNull(message.value)
        assertNull(message.prompt)
        assertNull(message.bucketContainer)
        assertNull(message.bucketKey)
        assertNull(message.version)
    }

    @Test
    fun `should create Output type message`() {
        val message =
            WebSocketMessageDTO(
                type = WebSocketMessageType.Output,
                value = "Hello World",
            )

        assertEquals(WebSocketMessageType.Output, message.type)
        assertEquals("Hello World", message.value)
    }

    @Test
    fun `should create InputRequest type message`() {
        val message =
            WebSocketMessageDTO(
                type = WebSocketMessageType.InputRequest,
                prompt = "Enter your name:",
            )

        assertEquals(WebSocketMessageType.InputRequest, message.type)
        assertEquals("Enter your name:", message.prompt)
    }

    @Test
    fun `should create Error type message`() {
        val message =
            WebSocketMessageDTO(
                type = WebSocketMessageType.Error,
                value = "Syntax error",
            )

        assertEquals(WebSocketMessageType.Error, message.type)
        assertEquals("Syntax error", message.value)
    }

    @Test
    fun `should create ExecutionFinished type message`() {
        val message =
            WebSocketMessageDTO(
                type = WebSocketMessageType.ExecutionFinished,
            )

        assertEquals(WebSocketMessageType.ExecutionFinished, message.type)
    }

    @Test
    fun `should create InputResponse type message`() {
        val message =
            WebSocketMessageDTO(
                type = WebSocketMessageType.InputResponse,
                value = "user input",
            )

        assertEquals(WebSocketMessageType.InputResponse, message.type)
        assertEquals("user input", message.value)
    }

    @Test
    fun `should create InitExecution type message`() {
        val message =
            WebSocketMessageDTO(
                type = WebSocketMessageType.InitExecution,
                bucketContainer = "snippets",
                bucketKey = "snippet-123",
                version = "1.1",
            )

        assertEquals(WebSocketMessageType.InitExecution, message.type)
        assertEquals("snippets", message.bucketContainer)
        assertEquals("snippet-123", message.bucketKey)
        assertEquals("1.1", message.version)
    }

    @Test
    fun `should support copy functionality`() {
        val original =
            WebSocketMessageDTO(
                type = WebSocketMessageType.Output,
                value = "original",
            )

        val copied =
            original.copy(
                type = WebSocketMessageType.Error,
                value = "modified",
            )

        assertEquals(WebSocketMessageType.Error, copied.type)
        assertEquals("modified", copied.value)
    }
}
