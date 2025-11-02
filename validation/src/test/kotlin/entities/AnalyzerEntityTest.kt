package entities

import checkers.IdentifierStyle
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AnalyzerEntityTest {

    @Test
    fun `AnalyzerEntity creates with default values`() {
        val entity = AnalyzerEntity(userId = "user123")

        assertEquals("user123", entity.userId)
        assertEquals(IdentifierStyle.NO_STYLE, entity.identifierStyle)
        assertTrue(entity.restrictPrintlnArgs)
        assertFalse(entity.restrictReadInputArgs)
        assertFalse(entity.noReadInput)
    }

    @Test
    fun `AnalyzerEntity creates with custom values`() {
        val entity =
            AnalyzerEntity(
                userId = "user456",
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = true,
                noReadInput = true,
            )

        assertEquals("user456", entity.userId)
        assertEquals(IdentifierStyle.CAMEL_CASE, entity.identifierStyle)
        assertFalse(entity.restrictPrintlnArgs)
        assertTrue(entity.restrictReadInputArgs)
        assertTrue(entity.noReadInput)
    }

    @Test
    fun `AnalyzerEntity copy works correctly`() {
        val original =
            AnalyzerEntity(
                userId = "user1",
                identifierStyle = IdentifierStyle.SNAKE_CASE,
            )
        val copy = original.copy(restrictPrintlnArgs = false)

        assertEquals("user1", copy.userId)
        assertEquals(IdentifierStyle.SNAKE_CASE, copy.identifierStyle)
        assertFalse(copy.restrictPrintlnArgs)
    }

    @Test
    fun `AnalyzerEntity equality works`() {
        val e1 = AnalyzerEntity("user1", IdentifierStyle.CAMEL_CASE, true, false, false)
        val e2 = AnalyzerEntity("user1", IdentifierStyle.CAMEL_CASE, true, false, false)
        val e3 = AnalyzerEntity("user2", IdentifierStyle.CAMEL_CASE, true, false, false)

        assertEquals(e1, e2)
        assertNotEquals(e1, e3)
    }

    @Test
    fun `AnalyzerEntity supports all IdentifierStyle values`() {
        val styles =
            listOf(
                IdentifierStyle.NO_STYLE,
                IdentifierStyle.CAMEL_CASE,
                IdentifierStyle.SNAKE_CASE,
            )

        styles.forEach { style ->
            val entity = AnalyzerEntity("user", style)
            assertEquals(style, entity.identifierStyle)
        }
    }

    @Test
    fun `AnalyzerEntity boolean flags work independently`() {
        val entity =
            AnalyzerEntity(
                userId = "user",
                restrictPrintlnArgs = true,
                restrictReadInputArgs = false,
                noReadInput = true,
            )

        assertTrue(entity.restrictPrintlnArgs)
        assertFalse(entity.restrictReadInputArgs)
        assertTrue(entity.noReadInput)
    }
}
