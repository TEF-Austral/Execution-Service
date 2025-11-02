package dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class ValidationResultDTOTest {

    @Test
    fun `ValidationResultDTO Valid is singleton`() {
        val valid1 = ValidationResultDTO.Valid
        val valid2 = ValidationResultDTO.Valid

        assertSame(valid1, valid2)
    }

    @Test
    fun `ValidationResultDTO Invalid creates with violations`() {
        val violations =
            listOf(
                LintViolationDTO("Error 1", 1, 1),
                LintViolationDTO("Error 2", 2, 2),
            )
        val invalid = ValidationResultDTO.Invalid(violations)

        assertEquals(2, invalid.violations.size)
        assertEquals("Error 1", invalid.violations[0].message)
        assertEquals("Error 2", invalid.violations[1].message)
    }

    @Test
    fun `ValidationResultDTO Invalid handles empty violations list`() {
        val invalid = ValidationResultDTO.Invalid(emptyList())

        assertTrue(invalid.violations.isEmpty())
    }

    @Test
    fun `ValidationResultDTO Invalid copy works correctly`() {
        val violations = listOf(LintViolationDTO("Error", 1, 1))
        val original = ValidationResultDTO.Invalid(violations)
        val newViolations = listOf(LintViolationDTO("New Error", 2, 2))
        val copy = original.copy(violations = newViolations)

        assertEquals(1, copy.violations.size)
        assertEquals("New Error", copy.violations[0].message)
    }

    @Test
    fun `ValidationResultDTO Invalid equality works`() {
        val v1 = ValidationResultDTO.Invalid(listOf(LintViolationDTO("Error", 1, 1)))
        val v2 = ValidationResultDTO.Invalid(listOf(LintViolationDTO("Error", 1, 1)))
        val v3 = ValidationResultDTO.Invalid(listOf(LintViolationDTO("Different", 1, 1)))

        assertEquals(v1, v2)
        assertNotEquals(v1, v3)
    }
}
