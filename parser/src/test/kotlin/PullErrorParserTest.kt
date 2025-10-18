import builder.DefaultNodeBuilder
import coordinates.Position
import node.EmptyExpression
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import parser.factory.VOnePointOneParserFactory
import type.CommonTypes

class PullErrorParserTest {

    private val factory = VOnePointOneParserFactory()

    @Test
    fun `test next should fail on invalid declaration missing type`() {
        // Setup: Tokens para una sentencia inválida: let x : = 5;
        val tokens =
            listOf(
                PrintScriptToken(
                    CommonTypes.LET,
                    "let",
                    Position(1, 1),
                ),
                PrintScriptToken(
                    CommonTypes.IDENTIFIER,
                    "x",
                    Position(1, 5),
                ),
                PrintScriptToken(
                    CommonTypes.DELIMITERS,
                    ":",
                    Position(1, 7),
                ),
                PrintScriptToken(
                    CommonTypes.ASSIGNMENT,
                    "=",
                    Position(1, 9),
                ), // Error: falta el tipo de dato
                PrintScriptToken(
                    CommonTypes.NUMBER_LITERAL,
                    "5",
                    Position(1, 11),
                ),
                PrintScriptToken(
                    CommonTypes.DELIMITERS,
                    ";",
                    Position(1, 12),
                ),
            )
        val parser = factory.createParser(MockTokenStream(tokens), DefaultNodeBuilder())

        // Act: Llamamos a next()
        val result = parser.next()

        // Assert: Verificamos que el resultado es un fallo y el mensaje es descriptivo.
        assertFalse(result.isSuccess, "Parsing should fail for an invalid statement")
        assertTrue(
            result.node is EmptyExpression,
            "The node should be an EmptyExpression on failure",
        )

        val expectedMessage =
            "Failed to parse the next node: Can't be " +
                "handled currently by the statement parser"
        assertEquals(expectedMessage, result.message)
    }

    @Test
    fun `test next should fail when statement is incomplete`() {
        // Setup: Tokens para una sentencia incompleta: print("hello"
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 6)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "hello", Position(1, 7)),
                // Error: faltan ')' y ';'
            )
        val parser = factory.createParser(MockTokenStream(tokens), DefaultNodeBuilder())

        // Act
        val result = parser.next()

        // Assert
        assertFalse(result.isSuccess, "Parsing should fail for an incomplete statement")
        assertTrue(result.node is EmptyExpression, "Node should be EmptyExpression on failure")

        val expectedMessage = "Failed to parse the next node: Was expecting closing parenthesis"
        assertEquals(expectedMessage, result.message)
    }

    @Test
    fun `test next should fail on an unexpected token`() {
        // Setup: Tokens que no pueden iniciar una sentencia, como un operador.
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.OPERATORS, "*", Position(1, 1)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 3)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 4)),
            )
        val parser = factory.createParser(MockTokenStream(tokens), DefaultNodeBuilder())

        // Act
        val result = parser.next()

        // Assert
        assertFalse(result.isSuccess, "Parsing should fail on an unexpected starting token")
        assertTrue(result.node is EmptyExpression, "Node should be EmptyExpression on failure")

        assertEquals("Failed to parse the next node: Invalid structure", result.message)
    }
}
