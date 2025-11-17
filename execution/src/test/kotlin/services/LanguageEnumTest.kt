package services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LanguageEnumTest {

    @Test
    fun `Language enum should have PRINTSCRIPT value`() {
        val language = Language.PRINTSCRIPT
        assertEquals("PRINTSCRIPT", language.name)
    }

    @Test
    fun `Language values should contain PRINTSCRIPT`() {
        val entries = Language.entries
        assertEquals(1, entries.size)
        assertEquals(Language.PRINTSCRIPT, entries[0])
    }

    @Test
    fun `Language valueOf should return PRINTSCRIPT`() {
        val language = Language.valueOf("PRINTSCRIPT")
        assertEquals(Language.PRINTSCRIPT, language)
    }
}
