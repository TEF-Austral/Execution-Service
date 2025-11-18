package utils

import emitter.Emitter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class InputProviderAdapterTest {

    private lateinit var adapter: InputProviderAdapter
    private lateinit var receiver: InputReceiver
    private lateinit var emitter: Emitter

    @BeforeEach
    fun setup() {
        receiver = mock(InputReceiver::class.java)
        emitter = mock(Emitter::class.java)
        adapter = InputProviderAdapter(receiver, emitter)
    }

    @Test
    fun testInput() {
        val name = "testInput"
        val expectedValue = "5"

        `when`(receiver.input(name)).thenReturn(expectedValue)

        val result = adapter.input(name)

        verify(emitter).stringEmit(name)
        verify(receiver).input(name)
        assertEquals(true, result.interpretedCorrectly)
        assertEquals("Success", result.message)
        assertEquals(expectedValue, result.interpreter!!.getValue())
    }

    @Test
    fun testInputWithDifferentName() {
        val name = "userInput"
        val expectedValue = "Hello"

        `when`(receiver.input(name)).thenReturn(expectedValue)

        val result = adapter.input(name)

        verify(emitter).stringEmit(name)
        verify(receiver).input(name)
        assertEquals(true, result.interpretedCorrectly)
        assertEquals(expectedValue, result.interpreter!!.getValue())
    }
}
