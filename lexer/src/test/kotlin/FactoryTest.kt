import coordinates.Position
import factory.StringToTokenConverterFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import type.CommonTypes

class FactoryTest {
    @Test
    fun createVersionOneShouldIncludeAllExpectedConverters() {
        val tokenConverter = StringToTokenConverterFactory.createVersionOne()

        val position = Position(1, 1)

        val dataTypeToken = tokenConverter.convert("number", position)
        assertEquals(CommonTypes.NUMBER, dataTypeToken.getType())

        val numberLiteralToken = tokenConverter.convert("123", position)
        assertEquals(CommonTypes.NUMBER_LITERAL, numberLiteralToken.getType())

        val stringLiteralToken = tokenConverter.convert("\"hello\"", position)
        assertEquals(CommonTypes.STRING_LITERAL, stringLiteralToken.getType())

        val letDeclarationToken = tokenConverter.convert("let", position)
        assertEquals(CommonTypes.LET, letDeclarationToken.getType())

        val printToken = tokenConverter.convert("println", position)
        assertEquals(CommonTypes.PRINT, printToken.getType())

        val operatorToken = tokenConverter.convert("+", position)
        assertEquals(CommonTypes.OPERATORS, operatorToken.getType())

        val assignmentToken = tokenConverter.convert("=", position)
        assertEquals(CommonTypes.ASSIGNMENT, assignmentToken.getType())

        val delimiterToken = tokenConverter.convert("(", position)
        assertEquals(CommonTypes.DELIMITERS, delimiterToken.getType())
    }

    @Test
    fun createVersionOneShouldReturnIdentifierForUnknownTokens() {
        val tokenConverter = StringToTokenConverterFactory.createVersionOne()

        val position = Position(1, 1)

        val unknownToken = tokenConverter.convert("unknownToken", position)
        assertEquals(CommonTypes.IDENTIFIER, unknownToken.getType())
    }
}
