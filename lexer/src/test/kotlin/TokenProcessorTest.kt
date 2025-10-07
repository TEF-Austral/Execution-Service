import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue

import org.junit.jupiter.api.Test
import string.splitter.extractor.Extraction
import string.splitter.extractor.TokenExtractor
import string.streaming.Retry
import string.streaming.Skip
import string.streaming.SplitterState
import string.streaming.Success
import string.streaming.TokenProcessor

class TokenProcessorTest {

    private class FakeTokenExtractor(
        private val tokenToReturn: String,
    ) : TokenExtractor {
        override fun extract(
            input: String,
            index: Int,
        ): Extraction = Extraction.Token(tokenToReturn)
    }

    private class FakeSkipExtractor(
        private val textToSkip: String,
    ) : TokenExtractor {
        override fun extract(
            input: String,
            index: Int,
        ): Extraction = Extraction.Skip(textToSkip)
    }

    private object NoMatchExtractor : TokenExtractor {
        override fun extract(
            input: String,
            index: Int,
        ): Extraction = Extraction.NoMatch
    }

    @Test
    fun `process returns null when state has no data`() {
        val initialState = SplitterState(buffer = "")
        val processor = TokenProcessor(emptyList())
        val result = processor.process(initialState)
        assertNull(result)
    }

    @Test
    fun `process returns Success when an extractor finds a token`() {
        val fakeExtractor = FakeTokenExtractor("SELECT")
        val initialState = SplitterState(buffer = "SELECT * FROM users;")
        val processor = TokenProcessor(listOf(fakeExtractor))
        val result = processor.process(initialState)
        assertTrue(result is Success)
        assertEquals("SELECT", (result as Success).token)
        assertEquals(" * FROM users;", result.newState.buffer)
    }

    @Test
    fun `process returns Skip when an extractor returns Skip`() {
        val fakeExtractor = FakeSkipExtractor("  ")
        val initialState = SplitterState(buffer = "  SELECT")
        val processor = TokenProcessor(listOf(fakeExtractor))
        val result = processor.process(initialState)
        assertTrue(result is Skip)
        assertEquals("SELECT", (result as Skip).newState.buffer)
    }

    @Test
    fun `process tries the next extractor if the first one returns NoMatch`() {
        val extractor1 = NoMatchExtractor
        val extractor2 = FakeTokenExtractor("FROM")
        val initialState = SplitterState(buffer = "FROM users;")
        val processor = TokenProcessor(listOf(extractor1, extractor2))
        val result = processor.process(initialState)
        assertTrue(result is Success)
        assertEquals("FROM", (result as Success).token)
    }

    @Test
    fun `process returns Retry when no extractor matches`() {
        val extractor = NoMatchExtractor
        val initialState = SplitterState(buffer = "invalid_token")
        val processor = TokenProcessor(listOf(extractor))
        val result = processor.process(initialState)
        assertTrue(result is Retry)
        assertEquals("nvalid_token", (result as Retry).newState.buffer)
    }

    @Test
    fun `process returns Retry if the extractor list is empty`() {
        val initialState = SplitterState(buffer = "some_data")
        val processor = TokenProcessor(emptyList())
        val result = processor.process(initialState)
        assertTrue(result is Retry)
        assertEquals("ome_data", (result as Retry).newState.buffer)
    }
}
