import factory.StringToTokenConverterFactory
import converter.specific.DelimiterToToken
import converter.specific.NumberLiteralToToken
import converter.specific.OperatorToToken
import converter.specific.StringLiteralToToken
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import type.CommonTypes
import coordinates.Position

class StringToTokenConverterFactoryTest {
    private val position = Position(1, 1)

    @Test
    fun `createDefaultsTokenConverter should create converter with all default converters`() {
        val tokenConverter = StringToTokenConverterFactory.createDefaultsTokenConverter()

        val letToken = tokenConverter.convert("let", position)
        assertEquals(CommonTypes.LET, letToken.getType())

        val numberToken = tokenConverter.convert("42", position)
        assertEquals(CommonTypes.NUMBER_LITERAL, numberToken.getType())

        val stringToken = tokenConverter.convert("\"hello\"", position)
        assertEquals(CommonTypes.STRING_LITERAL, stringToken.getType())

        val operatorToken = tokenConverter.convert("+", position)
        assertEquals(CommonTypes.OPERATORS, operatorToken.getType())

        val delimiterToken = tokenConverter.convert("(", position)
        assertEquals(CommonTypes.DELIMITERS, delimiterToken.getType())

        val conditionalToken = tokenConverter.convert("if", position)
        assertEquals(CommonTypes.CONDITIONALS, conditionalToken.getType())
    }

    @Test
    fun `createCustomTokenConverter should only recognize specified converters`() {
        val customConverters =
            listOf(
                NumberLiteralToToken,
                StringLiteralToToken,
                DelimiterToToken,
                OperatorToToken,
            )

        val customTokenConverter =
            StringToTokenConverterFactory.createCustomTokenConverter(
                customConverters,
            )

        val numberToken = customTokenConverter.convert("123", position)
        assertEquals(CommonTypes.NUMBER_LITERAL, numberToken.getType())

        val stringToken = customTokenConverter.convert("'test'", position)
        assertEquals(CommonTypes.STRING_LITERAL, stringToken.getType())

        val delimiterToken = customTokenConverter.convert(")", position)
        assertEquals(CommonTypes.DELIMITERS, delimiterToken.getType())

        val operatorToken = customTokenConverter.convert("*", position)
        assertEquals(CommonTypes.OPERATORS, operatorToken.getType())

        val declarationToken = customTokenConverter.convert("let", position)
        assertEquals(CommonTypes.IDENTIFIER, declarationToken.getType())

        val conditionalToken = customTokenConverter.convert("if", position)
        assertEquals(CommonTypes.IDENTIFIER, conditionalToken.getType())

        val functionToken = customTokenConverter.convert("function", position)
        assertEquals(CommonTypes.IDENTIFIER, functionToken.getType())
    }

    @Test
    fun `createCustomTokenConverter with empty list should only recognize identifiers`() {
        val customTokenConverter =
            StringToTokenConverterFactory.createCustomTokenConverter(
                emptyList(),
            )

        val token1 = customTokenConverter.convert("let", position)
        assertEquals(CommonTypes.IDENTIFIER, token1.getType())

        val token2 = customTokenConverter.convert("123", position)
        assertEquals(CommonTypes.IDENTIFIER, token2.getType())

        val token3 = customTokenConverter.convert("if", position)
        assertEquals(CommonTypes.IDENTIFIER, token3.getType())

        val token4 = customTokenConverter.convert("+", position)
        assertEquals(CommonTypes.IDENTIFIER, token4.getType())
    }

    @Test
    fun `createCustomTokenConverter with single converter should only recognize that type`() {
        val customConverters = listOf(NumberLiteralToToken)
        val customTokenConverter =
            StringToTokenConverterFactory.createCustomTokenConverter(
                customConverters,
            )

        val numberToken = customTokenConverter.convert("456", position)
        assertEquals(CommonTypes.NUMBER_LITERAL, numberToken.getType())

        val floatToken = customTokenConverter.convert("3.14", position)
        assertEquals(CommonTypes.NUMBER_LITERAL, floatToken.getType())

        val stringToken = customTokenConverter.convert("\"hello\"", position)
        assertEquals(CommonTypes.IDENTIFIER, stringToken.getType())

        val operatorToken = customTokenConverter.convert("+", position)
        assertEquals(CommonTypes.IDENTIFIER, operatorToken.getType())

        val delimiterToken = customTokenConverter.convert("(", position)
        assertEquals(CommonTypes.IDENTIFIER, delimiterToken.getType())
    }
}
