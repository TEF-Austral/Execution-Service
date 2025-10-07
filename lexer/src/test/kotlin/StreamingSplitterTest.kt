import string.streaming.StreamingSplitter
import factory.SpecialCharsFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.io.StringReader
import string.splitter.extractor.CommentExtractor
import string.splitter.extractor.NumberExtractor
import string.splitter.extractor.SpecialTokenExtractor
import string.splitter.extractor.StringLiteralExtractor
import string.splitter.extractor.TokenExtractor
import string.splitter.extractor.WhitespaceExtractor
import string.splitter.extractor.WordExtractor

class StreamingSplitterTest {

    private fun createSplitter(input: String): StreamingSplitter {
        val specialChars = SpecialCharsFactory.createSpecialChars()
        val extractors: List<TokenExtractor> =
            listOf(
                WhitespaceExtractor,
                CommentExtractor,
                StringLiteralExtractor,
                NumberExtractor(),
                SpecialTokenExtractor(specialChars),
                WordExtractor(specialChars),
            )
        return StreamingSplitter(StringReader(input), extractors)
    }

    @Test
    fun `should extract simple tokens correctly`() {
        val splitter = createSplitter("let x = 5;")
        val results = mutableListOf<String>()
        var result = splitter.next()
        while (result != null) {
            results.add(result.string)
            result = result.splitter.next()
        }

        assertEquals(listOf("let", "x", "=", "5", ";"), results)
    }

    @Test
    fun `should skip comments and extra whitespace`() {
        val splitter = createSplitter("  // comment\n let /* block */ x ; ")
        val results = mutableListOf<String>()
        var result = splitter.next()
        while (result != null) {
            results.add(result.string)
            result = result.splitter.next()
        }

        assertEquals(listOf("let", "x", ";"), results)
    }

    @Test
    fun `should track coordinates correctly`() {
        val splitter = createSplitter("let\n  x = 5")

        val res1 = splitter.next()!!
        assertEquals("let", res1.string)
        assertEquals(1, res1.coordinates.getRow())
        assertEquals(1, res1.coordinates.getColumn())

        val res2 = res1.splitter.next()!!
        assertEquals("x", res2.string)
        assertEquals(2, res2.coordinates.getRow())
        assertEquals(3, res2.coordinates.getColumn())

        val res3 = res2.splitter.next()!!
        assertEquals("=", res3.string)
        assertEquals(2, res3.coordinates.getRow())
        assertEquals(5, res3.coordinates.getColumn())
    }

    @Test
    fun `should return null at the end of the stream`() {
        val splitter = createSplitter("end")

        val res1 = splitter.next()
        val finalStateSplitter = res1!!.splitter

        val res2 = finalStateSplitter.next()

        assertEquals("end", res1.string)
        assertNull(res2)
    }
}
