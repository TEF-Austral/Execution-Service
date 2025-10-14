import converter.TokenConverterRegistry
import converter.specific.AssignmentToToken
import converter.specific.ComparisonToToken
import converter.specific.ConditionalToToken
import converter.specific.LetDeclarationToToken
import converter.specific.DelimiterToToken
import converter.specific.LogicalOperatorToken
import converter.specific.NumberLiteralToToken
import converter.specific.NumberTypeToToken
import converter.specific.OperatorToToken
import converter.specific.PrintToToken
import converter.specific.ReadEnvToToken
import converter.specific.ReadInputToToken
import converter.specific.StringLiteralToToken
import converter.specific.StringTypeToToken
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import type.CommonTypes
import coordinates.Position

class TokenConverterTest {
    private val position = Position(1, 1)

    @Test
    fun `test DelimiterToToken`() {
        Assertions.assertTrue(DelimiterToToken.canHandle("("))
        Assertions.assertTrue(DelimiterToToken.canHandle(")"))
        Assertions.assertTrue(DelimiterToToken.canHandle("{"))
        Assertions.assertTrue(DelimiterToToken.canHandle("}"))
        Assertions.assertTrue(DelimiterToToken.canHandle(","))
        Assertions.assertTrue(DelimiterToToken.canHandle("."))
        Assertions.assertTrue(DelimiterToToken.canHandle(";"))
        Assertions.assertTrue(DelimiterToToken.canHandle(":"))
        Assertions.assertTrue(DelimiterToToken.canHandle("?"))
        Assertions.assertFalse(DelimiterToToken.canHandle("a"))
        val token = DelimiterToToken.convert("(", position)
        Assertions.assertEquals(CommonTypes.DELIMITERS, token.getType())
    }

    @Test
    fun `test OperatorToToken`() {
        Assertions.assertTrue(OperatorToToken.canHandle("+"))
        Assertions.assertTrue(OperatorToToken.canHandle("-"))
        Assertions.assertTrue(OperatorToToken.canHandle("*"))
        Assertions.assertTrue(OperatorToToken.canHandle("/"))
        Assertions.assertFalse(OperatorToToken.canHandle("="))
        val token = OperatorToToken.convert("+", position)
        Assertions.assertEquals(CommonTypes.OPERATORS, token.getType())
    }

    @Test
    fun `test AssignmentToToken`() {
        Assertions.assertTrue(AssignmentToToken.canHandle("="))
        Assertions.assertFalse(AssignmentToToken.canHandle("=="))
        val token = AssignmentToToken.convert("=", position)
        Assertions.assertEquals(CommonTypes.ASSIGNMENT, token.getType())
    }

    @Test
    fun `test ComparisonToToken`() {
        Assertions.assertTrue(ComparisonToToken.canHandle("=="))
        Assertions.assertTrue(ComparisonToToken.canHandle("!="))
        Assertions.assertTrue(ComparisonToToken.canHandle(">"))
        Assertions.assertTrue(ComparisonToToken.canHandle(">="))
        Assertions.assertTrue(ComparisonToToken.canHandle("<"))
        Assertions.assertTrue(ComparisonToToken.canHandle("<="))
        Assertions.assertFalse(ComparisonToToken.canHandle("="))
        val token = ComparisonToToken.convert("==", position)
        Assertions.assertEquals(CommonTypes.COMPARISON, token.getType())
    }

    @Test
    fun `test LogicalOperatorToken`() {
        Assertions.assertTrue(LogicalOperatorToken.canHandle("&&"))
        Assertions.assertTrue(LogicalOperatorToken.canHandle("||"))
        Assertions.assertFalse(LogicalOperatorToken.canHandle("&"))
        val token = LogicalOperatorToken.convert("&&", position)
        Assertions.assertEquals(CommonTypes.LOGICAL_OPERATORS, token.getType())
    }

    @Test
    fun `test ConditionalToToken`() {
        Assertions.assertTrue(ConditionalToToken.canHandle("if"))
        Assertions.assertTrue(ConditionalToToken.canHandle("else"))
        Assertions.assertFalse(ConditionalToToken.canHandle("for"))
        val token = ConditionalToToken.convert("if", position)
        Assertions.assertEquals(CommonTypes.CONDITIONALS, token.getType())
    }

    @Test
    fun `test PrintToToken`() {
        Assertions.assertTrue(PrintToToken.canHandle("println"))
        Assertions.assertFalse(PrintToToken.canHandle("print"))
        val token = PrintToToken.convert("println", position)
        Assertions.assertEquals(CommonTypes.PRINT, token.getType())
    }

    @Test
    fun `test readInputToToken`() {
        Assertions.assertTrue(ReadInputToToken.canHandle("readInput"))
        val token = ReadInputToToken.convert("readInput", position)
        Assertions.assertEquals(CommonTypes.READ_INPUT, token.getType())
    }

    @Test
    fun `test readEnvToToken`() {
        Assertions.assertTrue(ReadEnvToToken.canHandle("readEnv"))
        val token = ReadEnvToToken.convert("readEnv", position)
        Assertions.assertEquals(CommonTypes.READ_ENV, token.getType())
    }

    @Test
    fun `test DeclarationToToken`() {
        Assertions.assertTrue(LetDeclarationToToken.canHandle("let"))
        val token = LetDeclarationToToken.convert("let", position)
        Assertions.assertEquals(CommonTypes.LET, token.getType())
    }

    @Test
    fun `test NumberToToken`() {
        Assertions.assertTrue(NumberTypeToToken.canHandle("number"))
        Assertions.assertTrue(StringTypeToToken.canHandle("string"))
        val token = NumberTypeToToken.convert("number", position)
        Assertions.assertEquals(CommonTypes.NUMBER, token.getType())
    }

    @Test
    fun `test StringToToken`() {
        Assertions.assertTrue(StringTypeToToken.canHandle("string"))
        Assertions.assertTrue(NumberTypeToToken.canHandle("number"))
        val token = StringTypeToToken.convert("string", position)
        Assertions.assertEquals(CommonTypes.STRING, token.getType())
    }

    @Test
    fun `test NumberLiteralToToken`() {
        Assertions.assertTrue(NumberLiteralToToken.canHandle("123"))
        Assertions.assertTrue(NumberLiteralToToken.canHandle("123.45"))
        Assertions.assertFalse(NumberLiteralToToken.canHandle("abc"))
        val token = NumberLiteralToToken.convert("123", position)
        Assertions.assertEquals(CommonTypes.NUMBER_LITERAL, token.getType())
    }

    @Test
    fun `test StringLiteralToToken`() {
        Assertions.assertTrue(StringLiteralToToken.canHandle("'hello'"))
        Assertions.assertTrue(StringLiteralToToken.canHandle("\"hello\""))
        Assertions.assertFalse(StringLiteralToToken.canHandle("hello"))
        val token = StringLiteralToToken.convert("'hello'", position)
        Assertions.assertEquals(CommonTypes.STRING_LITERAL, token.getType())
    }

    @Test
    fun `test TokenConverterRegistry`() {
        val converters =
            listOf(
                NumberLiteralToToken,
                StringLiteralToToken,
                DelimiterToToken,
            )
        val registry = TokenConverterRegistry(converters)

        val openParen = registry.convert("(", position)
        Assertions.assertEquals(CommonTypes.DELIMITERS, openParen.getType())

        val number = registry.convert("123", position)
        Assertions.assertEquals(CommonTypes.NUMBER_LITERAL, number.getType())

        val identifier = registry.convert("someVar", position)
        Assertions.assertEquals(CommonTypes.IDENTIFIER, identifier.getType())
    }
}
