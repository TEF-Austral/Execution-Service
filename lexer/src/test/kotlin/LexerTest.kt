import factory.StringToTokenConverterFactory
import converter.TokenConverter
import factory.StringSplitterFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.StringReader
import org.junit.jupiter.api.assertNotNull
import type.CommonTypes

class LexerTest {

    private val tokenConverter: TokenConverter =
        StringToTokenConverterFactory
            .createDefaultsTokenConverter()

    private fun lex(input: String): List<Token> {
        val splitter = StringSplitterFactory.createStreamingSplitter(StringReader(input))
        var lexer: Lexer? = DefaultLexer(tokenConverter, splitter)

        val tokens = mutableListOf<Token>()
        while (lexer != null) {
            val result = lexer.next()
            if (result != null) {
                tokens.add(result.token)
                lexer = result.lexer
            } else {
                lexer = null
            }
        }
        return tokens
    }

    @Test
    fun tokenizeDeclarationAssignmentWithNumberLiteral() {
        val tokens = lex("let x = 5;")
        assertEquals(5, tokens.size)

        assertEquals(CommonTypes.LET, tokens[0].getType())
        assertEquals("let", tokens[0].getValue())

        assertEquals(CommonTypes.IDENTIFIER, tokens[1].getType())
        assertEquals("x", tokens[1].getValue())

        assertEquals(CommonTypes.ASSIGNMENT, tokens[2].getType())
        assertEquals(" = ", tokens[2].getValue())

        assertEquals(CommonTypes.NUMBER_LITERAL, tokens[3].getType())
        assertEquals("5", tokens[3].getValue())

        assertEquals(CommonTypes.DELIMITERS, tokens[4].getType())
        assertEquals(";", tokens[4].getValue())
    }

    @Test
    fun tokenizeDeclarationConstAssignmentWithNumberLiteral() {
        val tokens = lex("const x = 5;")
        assertEquals(5, tokens.size)

        assertEquals(CommonTypes.CONST, tokens[0].getType())
        assertEquals("const", tokens[0].getValue())

        assertEquals(CommonTypes.IDENTIFIER, tokens[1].getType())
        assertEquals("x", tokens[1].getValue())

        assertEquals(CommonTypes.ASSIGNMENT, tokens[2].getType())
        assertEquals(" = ", tokens[2].getValue())

        assertEquals(CommonTypes.NUMBER_LITERAL, tokens[3].getType())
        assertEquals("5", tokens[3].getValue())

        assertEquals(CommonTypes.DELIMITERS, tokens[4].getType())
        assertEquals(";", tokens[4].getValue())
    }

    @Test
    fun tokenizeFunctionInvocationWithStringLiteral() {
        val tokens = lex("""println("hi")""")
        assertEquals(4, tokens.size)

        assertEquals(CommonTypes.PRINT, tokens[0].getType())
        assertEquals("(", tokens[1].getValue())
        assertEquals("hi", tokens[2].getValue())
        assertEquals(")", tokens[3].getValue())
    }

    @Test
    fun tokenizeArithmeticAndDelimiters() {
        val tokens = lex("(1+2)*3")
        assertEquals(7, tokens.size)

        assertEquals("(", tokens[0].getValue())
        assertEquals("1", tokens[1].getValue())
        assertEquals("+", tokens[2].getValue())
        assertEquals("2", tokens[3].getValue())
        assertEquals(")", tokens[4].getValue())
        assertEquals("*", tokens[5].getValue())
        assertEquals("3", tokens[6].getValue())
    }

    @Test
    fun tokenizeUnknownAsIdentifier() {
        val tokens = lex("foo")
        assertEquals(1, tokens.size)
        assertEquals(CommonTypes.IDENTIFIER, tokens[0].getType())
        assertEquals("foo", tokens[0].getValue())
    }

    @Test
    fun tokenizeConditionalsComparisonsAndLogicalOperators() {
        val tokens = lex("if (a<=10 && b!=0) return a")

        assertEquals(CommonTypes.CONDITIONALS, tokens[0].getType())
        assertEquals("if", tokens[0].getValue())

        assertEquals(CommonTypes.DELIMITERS, tokens[1].getType())
        assertEquals("(", tokens[1].getValue())

        assertEquals(CommonTypes.IDENTIFIER, tokens[2].getType())
        assertEquals("a", tokens[2].getValue())

        assertEquals(CommonTypes.COMPARISON, tokens[3].getType())
        assertEquals("<=", tokens[3].getValue())

        assertEquals(CommonTypes.NUMBER_LITERAL, tokens[4].getType())
        assertEquals("10", tokens[4].getValue())

        assertEquals(CommonTypes.LOGICAL_OPERATORS, tokens[5].getType())
        assertEquals("&&", tokens[5].getValue())

        assertEquals(CommonTypes.IDENTIFIER, tokens[6].getType())
        assertEquals("b", tokens[6].getValue())

        assertEquals(CommonTypes.COMPARISON, tokens[7].getType())
        assertEquals("!=", tokens[7].getValue())

        assertEquals(CommonTypes.NUMBER_LITERAL, tokens[8].getType())
        assertEquals("0", tokens[8].getValue())

        assertEquals(CommonTypes.DELIMITERS, tokens[9].getType())
        assertEquals(")", tokens[9].getValue())

        assertEquals(CommonTypes.IDENTIFIER, tokens[11].getType())
        assertEquals("a", tokens[11].getValue())
    }

    @Test
    fun peekTest() {
        val input = "let x = 5;"
        val splitter = StringSplitterFactory.createStreamingSplitter(StringReader(input))
        var lexer: Lexer? = DefaultLexer(tokenConverter, splitter)

        val peek1 = lexer!!.peek()
        assertNotNull(peek1)
        assertEquals(CommonTypes.LET, peek1.getType())
        assertEquals("let", peek1.getValue())
    }

    @Test
    fun nextTest() {
        val input =
            "let something :string = \"a really cool thing\";\n" +
                "let another_thing: string = \"another really cool thing\";let twice_thing : string = \"another really cool thing twice\";let third_thing :string=\"another really cool thing three times\";"
        val tokens = lex(input)
        assertEquals(28, tokens.size)
    }
}
