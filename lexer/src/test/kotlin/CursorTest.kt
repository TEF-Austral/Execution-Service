import coordinates.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import string.streaming.Cursor

class CursorTest {

    @Test
    fun `advance increments column for simple text`() {
        val initialCursor = Cursor(Position(1, 1))
        val text = "test"
        val newCursor = initialCursor.advance(text)
        assertEquals(1, newCursor.position.getRow())
        assertEquals(5, newCursor.position.getColumn())
    }

    @Test
    fun `advance increments row and resets column for newline`() {
        val initialCursor = Cursor(Position(5, 8))
        val text = "abc\ndef"
        val newCursor = initialCursor.advance(text)
        assertEquals(6, newCursor.position.getRow())
        assertEquals(4, newCursor.position.getColumn())
    }

    @Test
    fun `advance ignores carriage return character`() {
        val initialCursor = Cursor(Position(2, 2))
        val text = "a\rbc"
        val newCursor = initialCursor.advance(text)
        assertEquals(2, newCursor.position.getRow())
        assertEquals(5, newCursor.position.getColumn())
    }

    @Test
    fun `advance with empty string returns cursor with same position`() {
        val initialCursor = Cursor(Position(10, 20))
        val newCursor = initialCursor.advance("")
        assertEquals(10, newCursor.position.getRow())
        assertEquals(20, newCursor.position.getColumn())
        assertEquals(initialCursor, newCursor)
    }

    @Test
    fun `advance operates immutably and does not change original cursor`() {
        val initialCursor = Cursor(Position(1, 1))
        initialCursor.advance("some text")
        assertEquals(1, initialCursor.position.getRow())
        assertEquals(1, initialCursor.position.getColumn())
    }

    @Test
    fun `advance correctly handles multiple newlines`() {
        val initialCursor = Cursor(Position(1, 1))
        val text = "line1\nline2\nline3"
        val newCursor = initialCursor.advance(text)
        assertEquals(3, newCursor.position.getRow())
        assertEquals(6, newCursor.position.getColumn())
    }
}
