import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LanguageTest {

    @Test
    fun testLanguageEnumValues() {
        val languages = Language.values()
        assertEquals(1, languages.size)
        assertEquals(Language.PRINTSCRIPT, languages[0])
    }

    @Test
    fun testLanguageEnumValueOf() {
        val language = Language.valueOf("PRINTSCRIPT")
        assertEquals(Language.PRINTSCRIPT, language)
    }

    @Test
    fun testLanguageEnumName() {
        assertEquals("PRINTSCRIPT", Language.PRINTSCRIPT.name)
    }
}
