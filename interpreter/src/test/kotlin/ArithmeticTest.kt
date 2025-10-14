import coordinates.Position
import factory.InterpreterFactoryVersionOnePointOne
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals
import node.AssignmentStatement
import node.BinaryExpression
import node.DeclarationStatement
import node.IdentifierExpression
import node.LiteralExpression
import node.PrintStatement
import node.Program
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import result.InterpreterResult
import stream.MockAstStream
import type.CommonTypes

class ArithmeticTest {
    @Test
    fun `Assign pi and divide by two prints 1_57`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program Pi\n Output: ")
        val case =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "pi",
                                    Position(1, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(1, 9),
                                ),
                            initialValue = null,
                            Position(0, 0),
                        ),
                        AssignmentStatement(
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "pi",
                                    Position(2, 1),
                                ),
                            value =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "3.14",
                                        Position(2, 6),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "pi",
                                                Position(3, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "/",
                                            Position(3, 12),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "2",
                                                Position(3, 14),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("Program executed successfully", result.message)
        val printed = outputStream.toString().trim()
        assertEquals("Program Pi\n Output: 1.57", printed)
    }
}
