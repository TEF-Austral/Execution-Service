import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import string.splitter.Splitter

class SplitterTest {
    @Test
    fun `splits input into tokens with correct coordinates`() {
        val splitter = Splitter(listOf('=', '+', '-', '*', '/', ';'))
        val input = "let x = 10;\nlet y = x + 5;"
        val tokens = splitter.split(input)

        assertEquals(12, tokens.size)
        assertEquals("let", tokens[0].first)
        assertEquals(1, tokens[0].second.getRow())
        assertEquals(1, tokens[0].second.getColumn())

        assertEquals("x", tokens[1].first)
        assertEquals(1, tokens[1].second.getRow())
        assertEquals(5, tokens[1].second.getColumn())

        assertEquals("=", tokens[2].first)
        assertEquals(1, tokens[2].second.getRow())
        assertEquals(7, tokens[2].second.getColumn())

        assertEquals("10", tokens[3].first)
        assertEquals(1, tokens[3].second.getRow())
        assertEquals(9, tokens[3].second.getColumn())

        assertEquals(";", tokens[4].first)
        assertEquals(1, tokens[4].second.getRow())
        assertEquals(11, tokens[4].second.getColumn())

        assertEquals("let", tokens[5].first)
        assertEquals(2, tokens[5].second.getRow())
        assertEquals(1, tokens[5].second.getColumn())

        assertEquals("y", tokens[6].first)
        assertEquals(2, tokens[6].second.getRow())
        assertEquals(5, tokens[6].second.getColumn())

        assertEquals("=", tokens[7].first)
        assertEquals(2, tokens[7].second.getRow())
        assertEquals(7, tokens[7].second.getColumn())

        assertEquals("x", tokens[8].first)
        assertEquals(2, tokens[8].second.getRow())
        assertEquals(9, tokens[8].second.getColumn())

        assertEquals("+", tokens[9].first)
        assertEquals(2, tokens[9].second.getRow())
        assertEquals(11, tokens[9].second.getColumn())

        assertEquals("5", tokens[10].first)
        assertEquals(2, tokens[10].second.getRow())
        assertEquals(13, tokens[10].second.getColumn())

        assertEquals(";", tokens[11].first)
        assertEquals(2, tokens[11].second.getRow())
        assertEquals(14, tokens[11].second.getColumn())
    }

    @Test
    fun `handles input with only whitespace`() {
        val splitter = Splitter(listOf('=', '+', '-', '*', '/', ';'))
        val input = " \n\t "
        val tokens = splitter.split(input)
        assertEquals(0, tokens.size)
    }

    @Test
    fun `handles input with special characters only`() {
        val splitter = Splitter(listOf('=', '+', '-', '*', '/', ';'))
        val input = "=+*/;"
        val tokens = splitter.split(input)

        assertEquals(5, tokens.size)
        assertEquals("=", tokens[0].first)
        assertEquals("+", tokens[1].first)
        assertEquals("*", tokens[2].first)
        assertEquals("/", tokens[3].first)
        assertEquals(";", tokens[4].first)
    }

    @Test
    fun `handles input with mixed words and special characters`() {
        val splitter = Splitter(listOf('=', '+', '-', '*', '/', ';'))
        val input = "var1=var2+3;"
        val tokens = splitter.split(input)

        assertEquals(6, tokens.size)
        assertEquals("var1", tokens[0].first)
        assertEquals("=", tokens[1].first)
        assertEquals("var2", tokens[2].first)
        assertEquals("+", tokens[3].first)
        assertEquals("3", tokens[4].first)
        assertEquals(";", tokens[5].first)
    }

    @Test
    fun `handles input with unsupported special characters`() {
        val splitter = Splitter(listOf('=', '+', '-', '*', '/', ';'))
        val input = "a@b#c"
        val tokens = splitter.split(input)

        assertEquals(1, tokens.size)
        assertEquals("a@b#c", tokens[0].first)
    }

    @Test
    fun `handles empty input`() {
        val splitter = Splitter(listOf('=', '+', '-', '*', '/', ';'))
        val input = ""
        val tokens = splitter.split(input)
        assertEquals(0, tokens.size)
    }

    @Test
    fun `handles !=`() {
        val splitter = Splitter(listOf('=', '+', '-', '*', '/', ';', '!', '<', '>'))
        val input = "a != b;"
        val tokens = splitter.split(input)
        print(tokens)

        assertEquals(4, tokens.size)
        assertEquals("a", tokens[0].first)
        assertEquals("!=", tokens[1].first)
        assertEquals("b", tokens[2].first)
        assertEquals(";", tokens[3].first)
    }

    @Test
    fun `handles complex expressions with multiple operators`() {
        val splitter = Splitter(listOf('=', '+', '-', '*', '/', ';', '!', '<', '>', '(', ')'))
        val input = "x = 5 + 3 * (2 - 1);"
        val tokens = splitter.split(input)

        assertEquals(12, tokens.size)
        assertEquals("x", tokens[0].first)
        assertEquals("=", tokens[1].first)
        assertEquals("5", tokens[2].first)
        assertEquals("+", tokens[3].first)
        assertEquals("3", tokens[4].first)
        assertEquals("*", tokens[5].first)
        assertEquals("(", tokens[6].first)
        assertEquals("2", tokens[7].first)
        assertEquals("-", tokens[8].first)
        assertEquals("1", tokens[9].first)
        assertEquals(")", tokens[10].first)
        assertEquals(";", tokens[11].first)
    }

    @Test
    fun `word extractor handles single character words`() {
        val splitter = Splitter(listOf('='))
        val input = "a=b"
        val tokens = splitter.split(input)

        assertEquals(3, tokens.size)
        assertEquals("a", tokens[0].first)
        assertEquals("=", tokens[1].first)
        assertEquals("b", tokens[2].first)
    }

    @Test
    fun `word extractor skips quotes and apostrophes`() {
        val splitter = Splitter(listOf('='))
        val input = "word \"string\" 'char'"
        val tokens = splitter.split(input)

        assertEquals(3, tokens.size)
        assertEquals("word", tokens[0].first)
        assertEquals("\"string\"", tokens[1].first)
        assertEquals("'char'", tokens[2].first)
    }

    @Test
    fun `word extractor handles underscores and alphanumeric`() {
        val splitter = Splitter(listOf('='))
        val input = "var_name123 = value_2"
        val tokens = splitter.split(input)

        assertEquals(3, tokens.size)
        assertEquals("var_name123", tokens[0].first)
        assertEquals("=", tokens[1].first)
        assertEquals("value_2", tokens[2].first)
    }

    @Test
    fun `word extractor stops at whitespace`() {
        val splitter = Splitter(listOf())
        val input = "word1 word2\tword3\nword4"
        val tokens = splitter.split(input)

        assertEquals(4, tokens.size)
        assertEquals("word1", tokens[0].first)
        assertEquals("word2", tokens[1].first)
        assertEquals("word3", tokens[2].first)
        assertEquals("word4", tokens[3].first)
    }

    @Test
    fun `word extractor stops at special characters`() {
        val splitter = Splitter(listOf('+', '-', '*'))
        val input = "word+another-third*fourth"
        val tokens = splitter.split(input)

        assertEquals(7, tokens.size)
        assertEquals("word", tokens[0].first)
        assertEquals("+", tokens[1].first)
        assertEquals("another", tokens[2].first)
        assertEquals("-", tokens[3].first)
        assertEquals("third", tokens[4].first)
        assertEquals("*", tokens[5].first)
        assertEquals("fourth", tokens[6].first)
    }

    @Test
    fun `number extractor handles integers`() {
        val splitter = Splitter(listOf('+'))
        val input = "123 + 456"
        val tokens = splitter.split(input)

        assertEquals(3, tokens.size)
        assertEquals("123", tokens[0].first)
        assertEquals("+", tokens[1].first)
        assertEquals("456", tokens[2].first)
    }

    @Test
    fun `number extractor handles decimal numbers`() {
        val splitter = Splitter(listOf('='))
        val input = "pi = 3.14159"
        val tokens = splitter.split(input)

        assertEquals(3, tokens.size)
        assertEquals("pi", tokens[0].first)
        assertEquals("=", tokens[1].first)
        assertEquals("3.14159", tokens[2].first)
    }

    @Test
    fun `number extractor handles zero and single digits`() {
        val splitter = Splitter(listOf(','))
        val input = "0,1,2,9"
        val tokens = splitter.split(input)

        assertEquals(7, tokens.size)
        assertEquals("0", tokens[0].first)
        assertEquals(",", tokens[1].first)
        assertEquals("1", tokens[2].first)
        assertEquals(",", tokens[3].first)
        assertEquals("2", tokens[4].first)
        assertEquals(",", tokens[5].first)
        assertEquals("9", tokens[6].first)
    }

    @Test
    fun `number extractor handles multiple decimal points correctly`() {
        val splitter = Splitter(listOf())
        val input = "3.14.159"
        val tokens = splitter.split(input)

        assertEquals(2, tokens.size)
        assertEquals("3.14", tokens[0].first)
        assertEquals(".159", tokens[1].first)
    }

    @Test
    fun `comment extractor handles single line comments`() {
        val splitter = Splitter(listOf(';'))
        val input = "code; // this is a comment\nmore code"
        val tokens = splitter.split(input)

        assertEquals(4, tokens.size)
        assertEquals("code", tokens[0].first)
        assertEquals(";", tokens[1].first)
        assertEquals("more", tokens[2].first)
        assertEquals("code", tokens[3].first)
    }

    @Test
    fun `comment extractor handles single line comment at end of file`() {
        val splitter = Splitter(listOf())
        val input = "code // comment at end"
        val tokens = splitter.split(input)

        assertEquals(1, tokens.size)
        assertEquals("code", tokens[0].first)
    }

    @Test
    fun `comment extractor handles block comments`() {
        val splitter = Splitter(listOf(';'))
        val input = "before; /* block comment */ after;"
        val tokens = splitter.split(input)

        assertEquals(4, tokens.size)
        assertEquals("before", tokens[0].first)
        assertEquals(";", tokens[1].first)
        assertEquals("after", tokens[2].first)
        assertEquals(";", tokens[3].first)
    }

    @Test
    fun `comment extractor handles multiline block comments`() {
        val splitter = Splitter(listOf())
        val input = "start /*\nmultiline\ncomment\n*/ end"
        val tokens = splitter.split(input)

        assertEquals(2, tokens.size)
        assertEquals("start", tokens[0].first)
        assertEquals("end", tokens[1].first)
    }

    @Test
    fun `comment extractor handles nested comment symbols`() {
        val splitter = Splitter(listOf())
        val input = "code /* comment with // inside */ more"
        val tokens = splitter.split(input)

        assertEquals(2, tokens.size)
        assertEquals("code", tokens[0].first)
        assertEquals("more", tokens[1].first)
    }

    @Test
    fun `comment extractor handles slash without second slash`() {
        val splitter = Splitter(listOf('/'))
        val input = "a / b"
        val tokens = splitter.split(input)

        assertEquals(3, tokens.size)
        assertEquals("a", tokens[0].first)
        assertEquals("/", tokens[1].first)
        assertEquals("b", tokens[2].first)
    }

    @Test
    fun `handles mixed numbers words and special chars with whitespace`() {
        val splitter = Splitter(listOf('=', '+', '*', '(', ')'))
        val input = "  result123  =  ( value_1  +  42.5  ) * count  "
        val tokens = splitter.split(input)

        assertEquals(9, tokens.size)
        assertEquals("result123", tokens[0].first)
        assertEquals("=", tokens[1].first)
        assertEquals("(", tokens[2].first)
        assertEquals("value_1", tokens[3].first)
        assertEquals("+", tokens[4].first)
        assertEquals("42.5", tokens[5].first)
        assertEquals(")", tokens[6].first)
        assertEquals("*", tokens[7].first)
        assertEquals("count", tokens[8].first)
    }

    @Test
    fun `handles edge case with single character at end`() {
        val splitter = Splitter(listOf())
        val input = "a"
        val tokens = splitter.split(input)

        assertEquals(1, tokens.size)
        assertEquals("a", tokens[0].first)
    }

    @Test
    fun `handles strings with escaped quotes inside words`() {
        val splitter = Splitter(listOf())
        val input = "word\\\"with\\\"quotes inside"
        val tokens = splitter.split(input)

        assertEquals(2, tokens.size)
        assertEquals("word\\\"with\\\"quotes", tokens[0].first)
        assertEquals("inside", tokens[1].first)
    }

    @Test
    fun `handles numbers followed immediately by words`() {
        val splitter = Splitter(listOf())
        val input = "123abc 456.78def"
        val tokens = splitter.split(input)

        assertEquals(4, tokens.size)
        assertEquals("123", tokens[0].first)
        assertEquals("abc", tokens[1].first)
        assertEquals("456.78", tokens[2].first)
        assertEquals("def", tokens[3].first)
    }

    @Test
    fun `handles very long tokens`() {
        val splitter = Splitter(listOf())
        val longWord = "a".repeat(1000)
        val input = "$longWord 123"
        val tokens = splitter.split(input)

        assertEquals(2, tokens.size)
        assertEquals(longWord, tokens[0].first)
        assertEquals("123", tokens[1].first)
    }

    @Test
    fun `handles input with only comments`() {
        val splitter = Splitter(listOf())
        val input = "// line comment\n/* block comment */"
        val tokens = splitter.split(input)

        assertEquals(0, tokens.size)
    }
}
