package dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AnalyzeResponseDTOTest {

    @Test
    fun `should create valid AnalyzeResponseDTO`() {
        val response =
            AnalyzeResponseDTO(
                isValid = true,
                violations = emptyList(),
            )

        assertTrue(response.isValid)
        assertTrue(response.violations.isEmpty())
    }

    @Test
    fun `should create invalid AnalyzeResponseDTO with violations`() {
        val violations =
            listOf(
                LintViolationDTO("Error 1", 1, 1),
                LintViolationDTO("Error 2", 2, 2),
            )

        val response =
            AnalyzeResponseDTO(
                isValid = false,
                violations = violations,
            )

        assertFalse(response.isValid)
        assertEquals(2, response.violations.size)
    }

    @Test
    fun `should support copy functionality`() {
        val original =
            AnalyzeResponseDTO(
                isValid = true,
                violations = emptyList(),
            )

        val copied = original.copy(isValid = false)

        assertFalse(copied.isValid)
        assertTrue(copied.violations.isEmpty())
    }
}
