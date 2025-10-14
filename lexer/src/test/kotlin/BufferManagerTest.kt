import org.junit.jupiter.api.Test
import java.io.IOException
import java.io.Reader
import java.io.StringReader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import string.streaming.BufferManager
import string.streaming.SplitterState

class BufferManagerTest {

    private class FailingReader : Reader() {
        override fun read(
            cbuf: CharArray,
            off: Int,
            len: Int,
        ): Int = fail("El Reader no debería ser invocado en este test.")

        override fun close() {}
    }

    private class EndOfFileReader : Reader() {
        override fun read(
            cbuf: CharArray,
            off: Int,
            len: Int,
        ): Int = -1

        override fun close() {}
    }

    private class ExceptionThrowingReader : Reader() {
        override fun read(
            cbuf: CharArray,
            off: Int,
            len: Int,
        ): Int = throw IOException("Simulated disk read error")

        override fun close() {}
    }

    @Test
    fun `ensureSufficientBuffer returns same state if logical unit is already complete`() {
        val bufferManager = BufferManager(FailingReader())
        val initialState = SplitterState(buffer = "select * from users;")
        val newState = bufferManager.ensureSufficientBuffer(initialState)
        assertSame(initialState, newState)
    }

    @Test
    fun `ensureSufficientBuffer returns same state if stream is already finished`() {
        val bufferManager = BufferManager(FailingReader())
        val initialState = SplitterState(buffer = "some data", isStreamFinished = true)
        val newState = bufferManager.ensureSufficientBuffer(initialState)
        assertSame(initialState, newState)
    }

    @Test
    fun `ensureSufficientBuffer reads from reader and appends to buffer if incomplete`() {
        val reader = StringReader(" from users;")
        val bufferManager = BufferManager(reader)
        val initialState = SplitterState(buffer = "select *")
        val newState = bufferManager.ensureSufficientBuffer(initialState)
        assertNotSame(initialState, newState)
        assertEquals("select * from users;", newState.buffer)
        assertFalse(newState.isStreamFinished)
    }

    @Test
    fun `ensureSufficientBuffer sets stream finished flag when reader returns -1`() {
        val bufferManager = BufferManager(EndOfFileReader())
        val initialState = SplitterState(buffer = "incomplete data")
        val newState = bufferManager.ensureSufficientBuffer(initialState)
        assertNotSame(initialState, newState)
        assertEquals("incomplete data", newState.buffer)
        assertTrue(newState.isStreamFinished)
    }

    @Test
    fun `ensureSufficientBuffer handles IO exception and sets stream finished`() {
        val bufferManager = BufferManager(ExceptionThrowingReader())
        val initialState = SplitterState(buffer = "data before error")
        val newState = bufferManager.ensureSufficientBuffer(initialState)
        assertNotSame(initialState, newState)
        assertEquals("data before error", newState.buffer)
        assertTrue(newState.isStreamFinished)
    }
}
