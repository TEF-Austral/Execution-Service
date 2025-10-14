import coordinates.Position
import factory.InterpreterFactoryVersionOnePointOne
import node.BinaryExpression
import node.DeclarationStatement
import node.IdentifierExpression
import node.IfStatement
import node.LiteralExpression
import node.PrintStatement
import node.Program
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import result.InterpreterResult
import type.CommonTypes
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import stream.MockAstStream

class IfStatementTest {
    @Test
    fun `If statement with true literal condition should execute then branch`() {
        /*
        Represents the code:
        if (true) {
            println("Then branch executed");
        } else {
            println("Else branch executed");
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
                                        CommonTypes.BOOLEAN_LITERAL,
                                        "true",
                                        Position(1, 4),
                                    ),
                                    Position(0, 0),
                                ),
                            consequence =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "Then branch executed",
                                            Position(2, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "Else branch executed",
                                            Position(4, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertTrue(result.interpretedCorrectly, "Program should execute successfully.")
        assertEquals("Then branch executed", outputStream.toString().trim())
    }

    @Test
    fun `If statement with false literal condition should execute else branch`() {
        /*
        Represents the code:
        if (false) {
            println("Then branch executed");
        } else {
            println("Else branch executed");
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
                                        CommonTypes.BOOLEAN_LITERAL,
                                        "false",
                                        Position(1, 4),
                                    ),
                                    Position(0, 0),
                                ),
                            consequence =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "Then branch executed",
                                            Position(2, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "Else branch executed",
                                            Position(4, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertTrue(result.interpretedCorrectly, "Program should execute successfully.")
        assertEquals("Else branch executed", outputStream.toString().trim())
    }

    @Test
    fun `If statement with true variable condition should execute then branch`() {
        /*
        Represents the code:
        let condition: Boolean = true;
        if (condition) {
            println("Condition is true");
        } else {
            println("Condition is false");
        }
         */
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val program =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "condition",
                                    Position(1, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.BOOLEAN,
                                    "boolean",
                                    Position(1, 16),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.BOOLEAN_LITERAL,
                                        "true",
                                        Position(1, 26),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        IfStatement(
                            condition =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "condition",
                                        Position(2, 4),
                                    ),
                                    Position(0, 0),
                                ),
                            consequence =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "Condition is true",
                                            Position(3, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "Condition is false",
                                            Position(5, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertTrue(result.interpretedCorrectly, "Program should execute successfully.")
        assertEquals("Condition is true", outputStream.toString().trim())
    }

    @Test
    fun `If statement with false variable condition should execute else branch`() {
        /*
        Represents the code:
        let condition: Boolean = false;
        if (condition) {
            println("Condition is true");
        } else {
            println("Condition is false");
        }
         */
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val program =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "condition",
                                    Position(1, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.BOOLEAN,
                                    "boolean",
                                    Position(1, 16),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.BOOLEAN_LITERAL,
                                        "false",
                                        Position(1, 26),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        IfStatement(
                            condition =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "condition",
                                        Position(2, 4),
                                    ),
                                    Position(0, 0),
                                ),
                            consequence =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "Condition is true",
                                            Position(3, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "Condition is false",
                                            Position(5, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertTrue(result.interpretedCorrectly, "Program should execute successfully.")
        assertEquals("Condition is false", outputStream.toString().trim())
    }

    @Test
    fun `If statement without else branch and true condition should execute`() {
        /*
        Represents the code:
        if (true) {
            println("Only then");
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
                                        CommonTypes.BOOLEAN_LITERAL,
                                        "true",
                                        Position(1, 4),
                                    ),
                                    Position(0, 0),
                                ),
                            consequence =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "Only then",
                                            Position(2, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative = null,
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertTrue(result.interpretedCorrectly, "Program should execute successfully.")
        assertEquals("Only then", outputStream.toString().trim())
    }

    @Test
    fun `If statement without else branch and false condition should not execute`() {
        /*
        Represents the code:
        if (false) {
            println("Should not print");
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
                                        CommonTypes.BOOLEAN_LITERAL,
                                        "false",
                                        Position(1, 4),
                                    ),
                                    Position(0, 0),
                                ),
                            consequence =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "Should not print",
                                            Position(2, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative = null,
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertTrue(result.interpretedCorrectly, "Program should execute successfully.")
        assertEquals("", outputStream.toString().trim(), "Output should be empty.")
    }

    @Test
    fun `Nested if statements should execute correctly`() {
        /*
        Represents the code:
        let outer: Boolean = true;
        let inner: Boolean = false;
        if (outer) {
            if (inner) {
                println("Inner then");
            } else {
                println("Inner else");
            }
        } else {
            println("Outer else");
        }
         */
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val program =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "outer",
                                    Position(1, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.BOOLEAN,
                                    "boolean",
                                    Position(1, 12),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.BOOLEAN_LITERAL,
                                        "true",
                                        Position(1, 22),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "inner",
                                    Position(2, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.BOOLEAN,
                                    "boolean",
                                    Position(2, 12),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.BOOLEAN_LITERAL,
                                        "false",
                                        Position(2, 22),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        IfStatement(
                            condition =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "outer",
                                        Position(3, 4),
                                    ),
                                    Position(0, 0),
                                ),
                            consequence =
                                IfStatement(
                                    condition =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "inner",
                                                Position(4, 8),
                                            ),
                                            Position(0, 0),
                                        ),
                                    consequence =
                                        PrintStatement(
                                            LiteralExpression(
                                                PrintScriptToken(
                                                    CommonTypes.STRING_LITERAL,
                                                    "Inner then",
                                                    Position(5, 17),
                                                ),
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    alternative =
                                        PrintStatement(
                                            LiteralExpression(
                                                PrintScriptToken(
                                                    CommonTypes.STRING_LITERAL,
                                                    "Inner else",
                                                    Position(7, 17),
                                                ),
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            alternative =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "Outer else",
                                            Position(9, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertTrue(result.interpretedCorrectly, "Program should execute successfully.")
        assertEquals("Inner else", outputStream.toString().trim())
    }

    // TESTS LÓGICOS
    @Test
    fun `Logical AND with two true values should execute then branch`() {
        // if (true && true) { println("both true"); } else { println("something false"); }
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
                                            CommonTypes.LOGICAL_OPERATORS,
                                            "&&",
                                            Position(1, 9),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "true",
                                                Position(1, 12),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            consequence =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "both true",
                                            Position(2, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "something false",
                                            Position(4, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("both true", outputStream.toString().trim())
    }

    @Test
    fun `Logical AND with one false value should execute else branch`() {
        // if (true && false) { println("both true"); } else { println("something false"); }
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
                                            CommonTypes.LOGICAL_OPERATORS,
                                            "&&",
                                            Position(1, 9),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "false",
                                                Position(1, 12),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            consequence =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "both true",
                                            Position(2, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "something false",
                                            Position(4, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("something false", outputStream.toString().trim())
    }

    @Test
    fun `Logical OR with one true value should execute then branch`() {
        // if (false || true) { println("at least one is true"); } else { println("both false"); }
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
                                                "false",
                                                Position(1, 4),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.LOGICAL_OPERATORS,
                                            "||",
                                            Position(1, 10),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "true",
                                                Position(1, 13),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            consequence =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "at least one is true",
                                            Position(2, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "both false",
                                            Position(4, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("at least one is true", outputStream.toString().trim())
    }

    @Test
    fun `Logical OR with two false values should execute else branch`() {
        // if (false || false) { println("at least one is true"); } else { println("both false"); }
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
                                                "false",
                                                Position(1, 4),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.LOGICAL_OPERATORS,
                                            "||",
                                            Position(1, 10),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "false",
                                                Position(1, 13),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            consequence =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "at least one is true",
                                            Position(2, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "both false",
                                            Position(4, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("both false", outputStream.toString().trim())
    }

    @Test
    fun `Complex nested logical expression should evaluate correctly`() {
        // let a = true; let b = false; if ((a && true) || b) { println("complex true"); } else { println("complex false"); }
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "a",
                                    Position(1, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.BOOLEAN,
                                    "boolean",
                                    Position(1, 8),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.BOOLEAN_LITERAL,
                                        "true",
                                        Position(1, 18),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "b",
                                    Position(2, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.BOOLEAN,
                                    "boolean",
                                    Position(2, 8),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.BOOLEAN_LITERAL,
                                        "false",
                                        Position(2, 18),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        IfStatement(
                            condition =
                                BinaryExpression(
                                    left =
                                        BinaryExpression(
                                            left =
                                                IdentifierExpression(
                                                    PrintScriptToken(
                                                        CommonTypes.IDENTIFIER,
                                                        "a",
                                                        Position(3, 5),
                                                    ),
                                                    Position(0, 0),
                                                ),
                                            operator =
                                                PrintScriptToken(
                                                    CommonTypes.LOGICAL_OPERATORS,
                                                    "&&",
                                                    Position(3, 9),
                                                ),
                                            right =
                                                LiteralExpression(
                                                    PrintScriptToken(
                                                        CommonTypes.BOOLEAN_LITERAL,
                                                        "true",
                                                        Position(3, 12),
                                                    ),
                                                    Position(0, 0),
                                                ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.LOGICAL_OPERATORS,
                                            "||",
                                            Position(3, 18),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "b",
                                                Position(3, 21),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            consequence =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "complex true",
                                            Position(4, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "complex false",
                                            Position(6, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("complex true", outputStream.toString().trim())
    }

    @Test
    fun `Greater than comparison with true result should execute then branch`() {
        // if (10 > 5) { println("correct"); } else { println("incorrect"); }
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
                                                CommonTypes.NUMBER_LITERAL,
                                                "5",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            consequence =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "correct",
                                            Position(2, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "incorrect",
                                            Position(4, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("correct", outputStream.toString().trim())
    }

    @Test
    fun `Less than comparison with false result should execute else branch`() {
        // if (10 < 5) { println("incorrect"); } else { println("correct"); }
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
                                            "<",
                                            Position(1, 7),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "5",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            consequence =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "incorrect",
                                            Position(2, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "correct",
                                            Position(4, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("correct", outputStream.toString().trim())
    }

    @Test
    fun `Equal to comparison with variables should execute then branch`() {
        // let a = 5; let b = 5; if (a == b) { println("equal"); } else { println("not equal"); }
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "a",
                                    Position(1, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(1, 8),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "5",
                                        Position(1, 17),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "b",
                                    Position(2, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(2, 8),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "5",
                                        Position(2, 17),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        IfStatement(
                            condition =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "a",
                                                Position(3, 4),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "==",
                                            Position(3, 6),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "b",
                                                Position(3, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            consequence =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "equal",
                                            Position(4, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "not equal",
                                            Position(6, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("equal", outputStream.toString().trim())
    }

    @Test
    fun `Greater than or equal comparison should execute then branch`() {
        // if (10 >= 10) { println("correct"); } else { println("incorrect"); }
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
                                            ">=",
                                            Position(1, 7),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "10",
                                                Position(1, 10),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            consequence =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "correct",
                                            Position(2, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            alternative =
                                PrintStatement(
                                    LiteralExpression(
                                        PrintScriptToken(
                                            CommonTypes.STRING_LITERAL,
                                            "incorrect",
                                            Position(4, 13),
                                        ),
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(program)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("correct", outputStream.toString().trim())
    }
}
