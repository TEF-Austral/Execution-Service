package dtos

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class LintViolationDTOTest {

    @Test
    fun `LintViolationDTO creates instance correctly`() {
        val violation =
            LintViolationDTO(
                message = "Test error",
                line = 10,
                column = 5,
            )

        assertEquals("Test error", violation.message)
        assertEquals(10, violation.line)
        assertEquals(5, violation.column)
    }

    @Test
    fun `LintViolationDTO handles negative positions`() {
        val violation =
            LintViolationDTO(
                message = "Unknown error",
                line = -1,
                column = -1,
            )

        assertEquals(-1, violation.line)
        assertEquals(-1, violation.column)
    }

    @Test
    fun `LintViolationDTO copy works correctly`() {
        val original = LintViolationDTO("Error", 5, 10)
        val copy = original.copy(message = "New error")

        assertEquals("New error", copy.message)
        assertEquals(5, copy.line)
        assertEquals(10, copy.column)
    }

    @Test
    fun `LintViolationDTO equality works`() {
        val v1 = LintViolationDTO("Error", 5, 10)
        val v2 = LintViolationDTO("Error", 5, 10)
        val v3 = LintViolationDTO("Different", 5, 10)

        assertEquals(v1, v2)
        assertNotEquals(v1, v3)
    }
}
