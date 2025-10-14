import org.junit.jupiter.api.Test
import factory.StringSplitterFactory
import factory.StringToTokenConverterFactory
import java.io.StringReader
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import stream.token.LexerTokenStream

class LexerTokenStreamTest {

    @Test
    fun isAtEndReturnsTrueWhenNoTokensRemainWithDefaultLexer() {
        val reader = StringReader("")
        val lexer =
            DefaultLexer(
                StringToTokenConverterFactory.createDefaultsTokenConverter(),
                StringSplitterFactory.createStreamingSplitter(reader),
            )
        val stream = LexerTokenStream(lexer)
        assertTrue(stream.isAtEnd())
    }

    @Test
    fun isAtEndReturnsFalseWhenTokensRemainWithDefaultLexer() {
        val reader = StringReader("token")
        val lexer =
            DefaultLexer(
                StringToTokenConverterFactory.createDefaultsTokenConverter(),
                StringSplitterFactory.createStreamingSplitter(reader),
            )
        val stream = LexerTokenStream(lexer)
        assertFalse(stream.isAtEnd())
    }

    @Test
    fun peakReturnsCurrentTokenWithDefaultLexer() {
        val reader = StringReader("token")
        val lexer =
            DefaultLexer(
                StringToTokenConverterFactory.createDefaultsTokenConverter(),
                StringSplitterFactory.createStreamingSplitter(reader),
            )
        val stream = LexerTokenStream(lexer)
        assertNotNull(stream.peak())
    }

    @Test
    fun nextReturnsNullWhenNoTokensRemainWithDefaultLexer() {
        val reader = StringReader("")
        val lexer =
            DefaultLexer(
                StringToTokenConverterFactory.createDefaultsTokenConverter(),
                StringSplitterFactory.createStreamingSplitter(reader),
            )
        val stream = LexerTokenStream(lexer)
        assertNull(stream.next())
    }

    @Test
    fun nextReturnsStreamResultWithTokenAndNextStreamWithDefaultLexer() {
        val reader = StringReader("token")
        val lexer =
            DefaultLexer(
                StringToTokenConverterFactory.createDefaultsTokenConverter(),
                StringSplitterFactory.createStreamingSplitter(reader),
            )
        val stream = LexerTokenStream(lexer)
        val result = stream.next()
        assertNotNull(result)
        assertNotNull(result?.token)
        assertTrue(result?.nextStream is LexerTokenStream)
    }
}
