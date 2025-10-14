import coordinates.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import string.streaming.Cursor
import string.streaming.SplitterState

class SplitterStateTest {

    @Test
    fun `hasData is true when buffer is not empty`() {
        val state = SplitterState(buffer = "some data")
        assertTrue(state.hasData)
    }

    @Test
    fun `hasData is false when buffer is empty`() {
        val state = SplitterState(buffer = "")
        assertFalse(state.hasData)
    }

    @Test
    fun `hasCompleteLogicalUnit is true when stream is finished`() {
        val state = SplitterState(buffer = "incomplete", isStreamFinished = true)
        assertTrue(state.hasCompleteLogicalUnit())
    }

    @Test
    fun `hasCompleteLogicalUnit is true when buffer contains a delimiter`() {
        val stateWithSemicolon = SplitterState(buffer = "SELECT * FROM users;")
        val stateWithNewline = SplitterState(buffer = "SELECT * FROM users\n")
        assertTrue(stateWithSemicolon.hasCompleteLogicalUnit())
        assertTrue(stateWithNewline.hasCompleteLogicalUnit())
    }

    @Test
    fun `hasCompleteLogicalUnit is true for custom delimiters`() {
        val state = SplitterState(buffer = "data|more-data", logicalUnit = listOf('|', '>'))
        assertTrue(state.hasCompleteLogicalUnit())
    }

    @Test
    fun `hasCompleteLogicalUnit is false for incomplete buffer`() {
        val state = SplitterState(buffer = "SELECT * FROM users")
        assertFalse(state.hasCompleteLogicalUnit())
    }

    @Test
    fun `consume reduces buffer and advances cursor correctly`() {
        val initialCursor = Cursor(Position(1, 1))
        val initialState = SplitterState(buffer = "SELECT * FROM users;", cursor = initialCursor)
        val newState = initialState.consume(7)
        assertEquals("* FROM users;", newState.buffer)
        assertEquals(1, newState.cursor.position.getRow())
        assertEquals(8, newState.cursor.position.getColumn())
    }

    @Test
    fun `consume throws exception when length is too large`() {
        val state = SplitterState(buffer = "short")
        assertThrows<IllegalArgumentException> {
            state.consume(10)
        }
    }

    @Test
    fun `consume maintains other state properties`() {
        val initialState = SplitterState(buffer = "some data", isStreamFinished = true)
        val newState = initialState.consume(5)
        assertTrue(newState.isStreamFinished)
        assertEquals(initialState.logicalUnit, newState.logicalUnit)
    }
}
