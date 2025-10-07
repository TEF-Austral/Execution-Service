import coordinates.Position
import factory.InterpreterFactoryVersionOnePointOne
import node.BinaryExpression
import node.IfStatement
import node.LiteralExpression
import node.Program
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import result.InterpreterResult
import type.CommonTypes
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import node.ExpressionStatement
import stream.MockAstStream

class IfStatementFailureTest {
    @Test
    fun `If statement with a number as condition should fail`() {
        /*
        Represents the code:
        if (123) {
            "This should not happen";
        }
         */
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val program =
            Program(
                statements =
                    listOf(
                        IfStatement(
                            condition =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "123",
                                        Position(1, 4),
                                    ),
                                    Position(0, 0),
                                ),
                            consequence =
                                ExpressionStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "This should not happen",
                                            Position(2, 5),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative = null,
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )

        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertFalse(result.interpretedCorrectly, "Interpretation should fail due to type mismatch.")
        assertEquals(
            "If condition must evaluate to a boolean value, but got: NUMBER_LITERAL.",
            result.message,
        )
    }

    @Test
    fun `If statement with a string as condition should fail`() {
        /*
        Represents the code:
        if ("not a boolean") {
            "This should not happen";
        }
         */
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val program =
            Program(
                statements =
                    listOf(
                        IfStatement(
                            condition =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.STRING_LITERAL,
                                        "not a boolean",
                                        Position(1, 4),
                                    ),
                                    Position(0, 0),
                                ),
                            consequence =
                                ExpressionStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "This should not happen",
                                            Position(2, 5),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative = null,
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val mockAstStream = MockAstStream(program)

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertFalse(result.interpretedCorrectly, "Interpretation should fail due to type mismatch.")
        assertEquals(
            "If condition must evaluate to a boolean value, but got: STRING_LITERAL.",
            result.message,
        )
    }

    // TESTS DE FALLA LÓGICOS
    @Test
    fun `Logical AND with non-boolean type on the right should fail`() {
        // if (true && 123) { }
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
            Program(
                statements =
                    listOf(
                        IfStatement(
                            condition =
                                BinaryExpression(
                                    left =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "true",
                                                Position(1, 4),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "&&",
                                            Position(1, 9),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "123",
                                                Position(1, 12),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            consequence =
                                ExpressionStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "never happens",
                                            Position(2, 5),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative = null,
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val mockAstStream = MockAstStream(program)

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertFalse(
            result.interpretedCorrectly,
            "Interpretation should fail due to type mismatch in logical expression.",
        )
        assertEquals("Type mismatch: Incompatible types for LogicalAND operation", result.message)
    }

    @Test
    fun `Logical OR with non-boolean type on the left should fail`() {
        // if ("hello" || false) { }
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
            Program(
                statements =
                    listOf(
                        IfStatement(
                            condition =
                                BinaryExpression(
                                    left =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.STRING_LITERAL,
                                                "hello",
                                                Position(1, 4),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "||",
                                            Position(1, 12),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "false",
                                                Position(1, 15),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            consequence =
                                ExpressionStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "never happens",
                                            Position(2, 5),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative = null,
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val mockAstStream = MockAstStream(program)

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertFalse(
            result.interpretedCorrectly,
            "Interpretation should fail due to type mismatch in logical expression.",
        )
        assertEquals("Type mismatch: Incompatible types for LogicalOr operation", result.message)
    }

    // NUEVO TEST DE FALLA DE COMPARACIÓN
    @Test
    fun `Comparison between a number and a string should fail`() {
        // if (10 > "five") { }
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
            Program(
                statements =
                    listOf(
                        IfStatement(
                            condition =
                                BinaryExpression(
                                    left =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "10",
                                                Position(1, 4),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            ">",
                                            Position(1, 7),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.STRING_LITERAL,
                                                "five",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            consequence =
                                ExpressionStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "never happens",
                                            Position(2, 5),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative = null,
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val mockAstStream = MockAstStream(program)

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertFalse(
            result.interpretedCorrectly,
            "Interpretation should fail due to type mismatch in comparison.",
        )
        assertEquals(
            "Type mismatch: Incompatible types for > operation between NUMBER_LITERAL and STRING_LITERAL.",
            result.message,
        )
    }
}
