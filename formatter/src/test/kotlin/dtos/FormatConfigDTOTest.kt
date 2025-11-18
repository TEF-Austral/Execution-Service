package dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FormatConfigDTOTest {

    @Test
    fun `should create FormatConfigDTO with default values`() {
        val config = FormatConfigDTO()

        assertFalse(config.spaceBeforeColon)
        assertTrue(config.spaceAfterColon)
        assertTrue(config.spaceAroundAssignment)
        assertEquals(1, config.blankLinesAfterPrintln)
        assertEquals(4, config.indentSize)
        assertTrue(config.ifBraceOnSameLine)
        assertTrue(config.enforceSingleSpace)
        assertTrue(config.spaceAroundOperators)
    }

    @Test
    fun `should create FormatConfigDTO with custom values`() {
        val config =
            FormatConfigDTO(
                spaceBeforeColon = true,
                spaceAfterColon = false,
                spaceAroundAssignment = false,
                blankLinesAfterPrintln = 3,
                indentSize = 2,
                ifBraceOnSameLine = false,
                enforceSingleSpace = false,
                spaceAroundOperators = false,
            )

        assertTrue(config.spaceBeforeColon)
        assertFalse(config.spaceAfterColon)
        assertFalse(config.spaceAroundAssignment)
        assertEquals(3, config.blankLinesAfterPrintln)
        assertEquals(2, config.indentSize)
        assertFalse(config.ifBraceOnSameLine)
        assertFalse(config.enforceSingleSpace)
        assertFalse(config.spaceAroundOperators)
    }

    @Test
    fun `should support copy functionality`() {
        val original =
            FormatConfigDTO(
                indentSize = 4,
                blankLinesAfterPrintln = 1,
            )

        val copied =
            original.copy(
                indentSize = 8,
                spaceBeforeColon = true,
            )

        assertEquals(8, copied.indentSize)
        assertTrue(copied.spaceBeforeColon)
        assertEquals(1, copied.blankLinesAfterPrintln)
    }

    @Test
    fun `should create FormatConfigDTO with zero blank lines`() {
        val config =
            FormatConfigDTO(
                blankLinesAfterPrintln = 0,
            )

        assertEquals(0, config.blankLinesAfterPrintln)
    }

    @Test
    fun `should create FormatConfigDTO with large indent size`() {
        val config =
            FormatConfigDTO(
                indentSize = 8,
            )

        assertEquals(8, config.indentSize)
    }
}
