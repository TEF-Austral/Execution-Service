import result.InterpreterResult
import node.Program
import node.BinaryExpression
import node.IdentifierExpression
import node.LiteralExpression
import node.AssignmentStatement
import node.DeclarationStatement
import node.PrintStatement
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals
import type.CommonTypes
import coordinates.Position
import factory.InterpreterFactoryVersionOnePointOne
import node.ExpressionStatement
import node.IfStatement
import org.junit.jupiter.api.BeforeEach
import stream.MockAstStream

class InterpreterTest {
    @BeforeEach
    fun setUp() {
    }

    @Test
    fun `Variable Reassignment and Expression Evaluation`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 2\n Output: ")
        val case2 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "x",
                                    Position(1, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(1, 8),
                                ),
                            initialValue = null,
                            Position(0, 0),
                        ),
                        AssignmentStatement(
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "x",
                                    Position(2, 1),
                                ),
                            value =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "42",
                                        Position(2, 5),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "x",
                                        Position(3, 9),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case2)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 2\n Output: 42", printed)
    }

    @Test
    fun `Basic Variable Declaration and Assignment`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 1\n Output: ")
        val case1 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "x",
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
                                        "42",
                                        Position(1, 17),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "x",
                                        Position(2, 9),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case1)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 1\n Output: 42", printed)
    }

    @Test
    fun `Basic Arithmetics Operations`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 3\n Output: ")
        val case3 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "x",
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
                                        "42",
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
                                    "y",
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
                                        "10",
                                        Position(2, 17),
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
                                    "z",
                                    Position(3, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(3, 8),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "x",
                                                Position(3, 17),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(3, 19),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "y",
                                                Position(3, 21),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "z",
                                        Position(4, 9),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case3)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 3\n Output: 52", printed)
    }

    @Test
    fun `Basic Double Arithmetics Operations`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 4\n Output: ")
        val case4 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "x",
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
                                        "42.2",
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
                                    "y",
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
                                        "10.0",
                                        Position(2, 17),
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
                                    "z",
                                    Position(3, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(3, 8),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "x",
                                                Position(3, 17),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(3, 19),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "y",
                                                Position(3, 21),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "z",
                                        Position(4, 9),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case4)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 4\n Output: 52.2", printed)
    }

    @Test
    fun `Multiplication and Division Operations`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 5\n Output: ")
        val case5 =
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
                                        "6",
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
                                        "3",
                                        Position(2, 17),
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
                                    "c",
                                    Position(3, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(3, 8),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "a",
                                                Position(3, 17),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "*",
                                            Position(3, 19),
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
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "d",
                                    Position(4, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(4, 8),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "c",
                                                Position(4, 17),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "/",
                                            Position(4, 19),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "2",
                                                Position(4, 21),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "d",
                                        Position(5, 9),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case5)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 5\n Output: 9", printed)
    }

    @Test
    fun `Combined Arithmetic Operations`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 6\n Output: ")
        val case6 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "x",
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
                                        "8",
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
                                    "y",
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
                                        "2",
                                        Position(2, 17),
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
                                    "z",
                                    Position(3, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(3, 8),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        BinaryExpression(
                                            left =
                                                IdentifierExpression(
                                                    PrintScriptToken(
                                                        CommonTypes.IDENTIFIER,
                                                        "x",
                                                        Position(3, 17),
                                                    ),
                                                    Position(0, 0),
                                                ),
                                            operator =
                                                PrintScriptToken(
                                                    CommonTypes.OPERATORS,
                                                    "/",
                                                    Position(3, 19),
                                                ),
                                            right =
                                                IdentifierExpression(
                                                    PrintScriptToken(
                                                        CommonTypes.IDENTIFIER,
                                                        "y",
                                                        Position(3, 21),
                                                    ),
                                                    Position(0, 0),
                                                ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "*",
                                            Position(3, 23),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "5",
                                                Position(3, 25),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "z",
                                        Position(4, 9),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case6)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 6\n Output: 20", printed)
    }

    @Test
    fun `Complex Arithmetic with Multiple Variables`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 7\n Output: ")
        val case7 =
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
                                        "10",
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
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "c",
                                    Position(3, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(3, 8),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "2",
                                        Position(3, 17),
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
                                    "d",
                                    Position(4, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(4, 8),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "a",
                                                Position(4, 17),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "*",
                                            Position(4, 19),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "b",
                                                Position(4, 21),
                                            ),
                                            Position(0, 0),
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
                                    "e",
                                    Position(5, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(5, 8),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "d",
                                                Position(5, 17),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(5, 19),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "c",
                                                Position(5, 21),
                                            ),
                                            Position(0, 0),
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
                                    "f",
                                    Position(6, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(6, 8),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "e",
                                                Position(6, 17),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "/",
                                            Position(6, 19),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "2",
                                                Position(6, 21),
                                            ),
                                            Position(0, 0),
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
                                    "g",
                                    Position(7, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(7, 8),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "f",
                                                Position(7, 17),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "-",
                                            Position(7, 19),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "4",
                                                Position(7, 21),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "g",
                                        Position(8, 9),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case7)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 7\n Output: 22", printed)
    }

    @Test
    fun `Double Division Operation`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 8\n Output: ")
        val case8 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "a",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "10",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "b",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "3",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "c",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "a",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "/",
                                            Position(0, 0),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "b",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "c",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case8)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 8\n Output: 3.3333333333333335", printed)
    }

    @Test
    fun `Basic Arithmetics Operations with Doubles`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 9\n Output: ")
        val case9 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "x",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "42.0",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "y",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "10.0",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "z",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "x",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(0, 0),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "y",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "z",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
                Position(0, 0),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case9)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 9\n Output: 52.0", printed)
    }

    @Test
    fun `Divide Two Numbers Operation with Doubles`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 10\n Output: ")
        val case10 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "x",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "42.0",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "y",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "10.0",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "z",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "x",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "/",
                                            Position(0, 0),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "y",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "z",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
                Position(0, 0),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case10)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 10\n Output: 4.2", printed)
    }

    @Test
    fun `Multiplication Two Numbers Operation with Doubles`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 11\n Output: ")
        val case11 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "x",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "42.0",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "y",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "10.0",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "z",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "x",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "*",
                                            Position(0, 0),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "y",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "z",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
                Position(0, 0),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case11)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 11\n Output: 420.0", printed)
    }

    @Test
    fun `Subtraction Two Numbers Operation with Doubles`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 12\n Output: ")
        val case12 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "x",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "42.0",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "y",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "10.0",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "z",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "x",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "-",
                                            Position(0, 0),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "y",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "z",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
                Position(0, 0),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case12)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 12\n Output: 32.0", printed)
    }

    @Test
    fun `String Concatenation with Numbers`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 13\n Output: ")
        val case13 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "text",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.STRING,
                                    "string",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.STRING_LITERAL,
                                        "Hello",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "number",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "42",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "result",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.STRING,
                                    "string",
                                    Position(0, 0),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "text",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(0, 0),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "number",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "result",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
                Position(0, 0),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case13)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 13\n Output: Hello42", printed)
    }

    @Test
    fun `number Addition with String Concatenation`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 14\n Output: ")
        val case14 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "num1",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "10",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "num2",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "5",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "text",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.STRING,
                                    "string",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.STRING_LITERAL,
                                        " equals ",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "sum",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "num1",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(0, 0),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "num2",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "result",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.STRING,
                                    "string",
                                    Position(0, 0),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        BinaryExpression(
                                            left =
                                                IdentifierExpression(
                                                    PrintScriptToken(
                                                        CommonTypes.IDENTIFIER,
                                                        "num1",
                                                        Position(0, 0),
                                                    ),
                                                    Position(0, 0),
                                                ),
                                            operator =
                                                PrintScriptToken(
                                                    CommonTypes.OPERATORS,
                                                    "+",
                                                    Position(0, 0),
                                                ),
                                            right =
                                                IdentifierExpression(
                                                    PrintScriptToken(
                                                        CommonTypes.IDENTIFIER,
                                                        "text",
                                                        Position(0, 0),
                                                    ),
                                                    Position(0, 0),
                                                ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(0, 0),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "sum",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "result",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
                Position(0, 0),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case14)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 14\n Output: 10 equals 15", printed)
    }

    @Test
    fun `String and Double Concatenation`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 15\n Output: ")
        val case15 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "message",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.STRING,
                                    "string",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.STRING_LITERAL,
                                        "Pi is approximately ",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "pi",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "3.14159",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "result",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.STRING,
                                    "string",
                                    Position(0, 0),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "message",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(0, 0),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "pi",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "result",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
                Position(0, 0),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case15)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 15\n Output: Pi is approximately 3.14159", printed)
    }

    @Test
    fun `Complex Edge Cases Test`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 16\n Output: ")
        val case16 =
            Program(
                statements =
                    listOf(
                        // Test zero operations
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "zero",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "0",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "five",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "5",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        // Zero addition
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "zeroAdd",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "zero",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(0, 0),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "five",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        // Zero multiplication
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "zeroMult",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "zero",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "*",
                                            Position(0, 0),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "five",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        // Negative numbers
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "negative",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "-10",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "negativeResult",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "negative",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(0, 0),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "five",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        // Very small decimal
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "smallDecimal",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "0.001",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "precision",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "smallDecimal",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "*",
                                            Position(0, 0),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "1000",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        // Multiple variable reassignments
                        AssignmentStatement(
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "five",
                                    Position(0, 0),
                                ),
                            value =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "five",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "*",
                                            Position(0, 0),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "2",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        // Complex nested expression with mixed types
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "complexResult",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.STRING,
                                    "string",
                                    Position(0, 0),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        BinaryExpression(
                                            left =
                                                BinaryExpression(
                                                    left =
                                                        LiteralExpression(
                                                            PrintScriptToken(
                                                                CommonTypes.STRING_LITERAL,
                                                                "Results: ",
                                                                Position(0, 0),
                                                            ),
                                                            Position(0, 0),
                                                        ),
                                                    operator =
                                                        PrintScriptToken(
                                                            CommonTypes.OPERATORS,
                                                            "+",
                                                            Position(0, 0),
                                                        ),
                                                    right =
                                                        IdentifierExpression(
                                                            PrintScriptToken(
                                                                CommonTypes.IDENTIFIER,
                                                                "zeroAdd",
                                                                Position(0, 0),
                                                            ),
                                                            Position(0, 0),
                                                        ),
                                                    Position(0, 0),
                                                ),
                                            operator =
                                                PrintScriptToken(
                                                    CommonTypes.OPERATORS,
                                                    "+",
                                                    Position(0, 0),
                                                ),
                                            right =
                                                LiteralExpression(
                                                    PrintScriptToken(
                                                        CommonTypes.STRING_LITERAL,
                                                        ", ",
                                                        Position(0, 0),
                                                    ),
                                                    Position(0, 0),
                                                ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(0, 0),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "precision",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "complexResult",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
                Position(0, 0),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case16)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 16\n Output: Results: 5, 1.0", printed)
    }

    @Test
    fun `Empty String Concatenation`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 18\n Output: ")
        val case18 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "empty",
                                    Position(1, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.STRING,
                                    "string",
                                    Position(1, 12),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.STRING_LITERAL,
                                        "",
                                        Position(1, 21),
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
                                    "number",
                                    Position(2, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(2, 13),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "123",
                                        Position(2, 22),
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
                                    "text",
                                    Position(3, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.STRING,
                                    "string",
                                    Position(3, 11),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.STRING_LITERAL,
                                        "Hello",
                                        Position(3, 20),
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
                                    "result1",
                                    Position(4, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.STRING,
                                    "string",
                                    Position(4, 14),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "empty",
                                                Position(4, 23),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(4, 29),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "number",
                                                Position(4, 31),
                                            ),
                                            Position(0, 0),
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
                                    "result2",
                                    Position(5, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.STRING,
                                    "string",
                                    Position(5, 14),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "text",
                                                Position(5, 23),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(5, 28),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "empty",
                                                Position(5, 30),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                BinaryExpression(
                                    left =
                                        BinaryExpression(
                                            left =
                                                IdentifierExpression(
                                                    PrintScriptToken(
                                                        CommonTypes.IDENTIFIER,
                                                        "result1",
                                                        Position(6, 9),
                                                    ),
                                                    Position(0, 0),
                                                ),
                                            operator =
                                                PrintScriptToken(
                                                    CommonTypes.OPERATORS,
                                                    "+",
                                                    Position(6, 17),
                                                ),
                                            right =
                                                LiteralExpression(
                                                    PrintScriptToken(
                                                        CommonTypes.STRING_LITERAL,
                                                        " ",
                                                        Position(6, 19),
                                                    ),
                                                    Position(0, 0),
                                                ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(6, 23),
                                        ),
                                    right =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "result2",
                                                Position(6, 25),
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
        val mockAstStream = MockAstStream(case18)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals(result.message, "Program executed successfully")
        val printed = outputStream.toString().trim()
        assertEquals("Program 18\n Output: 123 Hello", printed)
    }

    @Test
    fun `Empty Program Should Pass`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val case = Program(statements = emptyList())

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertTrue(result.interpretedCorrectly)
        assertEquals("Program executed successfully", result.message)
    }

    @Test
    fun `Complex Variable Delcaration and Reassignment Should Work`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 19\n Output: ")
        val case19 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "x",
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
                                        "10",
                                        Position(1, 17),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        AssignmentStatement(
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "x",
                                    Position(2, 1),
                                ),
                            value =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "x",
                                                Position(2, 8),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(2, 10),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "5",
                                                Position(2, 12),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "x",
                                        Position(3, 9),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case19)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("Program executed successfully", result.message)
        val printed = outputStream.toString().trim()
        assertEquals("Program 19\n Output: 15", printed)
    }

    @Test
    fun `test program with useless expression statements runs correctly`() {
        // --- 1. Preparación (Setup) ---

        // Capturamos la salida de la consola para poder verificarla
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        // El nodo Program que creamos en el paso anterior
        val uselessExpressionsProgram =
            Program(
                statements =
                    listOf(
                        // let miVariable: number = 42;
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "miVariable",
                                    Position(1, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(1, 17),
                                ),
                            initialValue =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "42",
                                        Position(1, 26),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        // "un string";
                        ExpressionStatement(
                            expression =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.STRING_LITERAL,
                                        "un string",
                                        Position(2, 1),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        // 123.45;
                        ExpressionStatement(
                            expression =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.NUMBER_LITERAL,
                                        "123.45",
                                        Position(3, 1),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        // 10 + 5;
                        ExpressionStatement(
                            expression =
                                BinaryExpression(
                                    left =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "10",
                                                Position(4, 1),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(4, 4),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "5",
                                                Position(4, 6),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        // miVariable;
                        ExpressionStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "miVariable",
                                        Position(5, 1),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        // println(miVariable);
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "miVariable",
                                        Position(6, 9),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )

        // Asumimos que tienes una instancia del intérprete lista para usar
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()

        // Le pasamos el AST completo al intérprete
        val mockAstStream = MockAstStream(uselessExpressionsProgram)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        // --- 3. Verificación (Assertions) ---

        // Primero, verificamos que el intérprete terminó correctamente
        assertTrue(
            result.interpretedCorrectly,
            "El programa debería haberse ejecutado sin errores.",
        )
        assertEquals("Program executed successfully", result.message)

        // Segundo, verificamos que la salida en consola es la esperada
        val printedOutput = outputStream.toString().trim() // .trim() para quitar saltos de línea
        assertEquals("42", printedOutput, "La única salida debería ser el valor de 'miVariable'.")
    }

    @Test
    fun `Printing Null Variable Should Succeed`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val programWithNullVar =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "myVar",
                                    Position(1, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.STRING,
                                    "string",
                                    Position(1, 12),
                                ),
                            initialValue = null,
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "myVar",
                                        Position(2, 9),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(programWithNullVar)

        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertTrue(result.interpretedCorrectly, "El programa debería ejecutarse correctamente.")

        val printedOutput = outputStream.toString().trim()
        assertEquals("null", printedOutput, "La salida de una variable null debería ser 'null'.")
    }

    @Test
    fun `Print Simple Sum Without Declaration Should Succeed`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        // This program represents the code: println(5 + 10);
        val simpleSumProgram =
            Program(
                statements =
                    listOf(
                        PrintStatement(
                            expression =
                                BinaryExpression(
                                    left =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "5",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(1, 11),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "10",
                                                Position(1, 13),
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
        val mockAstStream = MockAstStream(simpleSumProgram)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        // Assert that the program executed without errors
        assertTrue(result.interpretedCorrectly, "The program should have executed successfully.")
        assertEquals("Program executed successfully", result.message)

        // Assert that the correct output was printed to the console
        val printedOutput = outputStream.toString().trim()
        assertEquals("15", printedOutput, "The output should be the result of the sum.")
    }

    @Test
    fun `Simple IfStatement Should Pass`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        /* if( true ){
            println("ENTRAAAA")
           }else {
                println("NO ENTRAAAA")
           }
        }
         * */

        val programWithStatement =
            Program(
                listOf(
                    IfStatement(
                        LiteralExpression(
                            PrintScriptToken(
                                CommonTypes.BOOLEAN_LITERAL,
                                "true",
                                Position(1, 1),
                            ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            LiteralExpression(
                                PrintScriptToken(
                                    CommonTypes.STRING_LITERAL,
                                    "ENTRAAAAAAAAAAAAAAAA",
                                    Position(1, 1),
                                ),
                                Position(0, 0),
                            ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            LiteralExpression(
                                PrintScriptToken(
                                    CommonTypes.STRING_LITERAL,
                                    "NO ENTRAAAAAAAAAAAAAAAA",
                                    Position(1, 1),
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

        val mockAstStream = MockAstStream(programWithStatement)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertTrue(result.interpretedCorrectly, "El programa debería ejecutarse correctamente.")
        assertEquals("Program executed successfully", result.message)
        val printedOutput = outputStream.toString().trim()
        assertEquals(
            "ENTRAAAAAAAAAAAAAAAA",
            printedOutput,
            "La salida debería ser 'ENTRAAAAAAAAAAAAAAAA'.",
        )
    }

    @Test
    fun `Simple Const Declaration Should Pass`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 2\n Output: ")
        val case1 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.CONST, "const", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "x",
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
                                        "42",
                                        Position(1, 17),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "x",
                                        Position(2, 9),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case1)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertEquals("Program executed successfully", result.message)
        assertTrue(result.interpretedCorrectly)
        val printed = outputStream.toString().trim()
        assertEquals("Program 2\n Output: 42", printed)
    }

    @Test
    fun `Simple Const Declaration and Sum Should Pass`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 2\n Output: ")
        val case1 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "x",
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
                                        "42",
                                        Position(1, 17),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                        AssignmentStatement(
                            PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(2, 1)),
                            value =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "x",
                                                Position(2, 8),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(2, 10),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "1",
                                                Position(2, 12),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(2, 16),
                                ),
                            Position(0, 0),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "x",
                                        Position(2, 9),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case1)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertEquals("Program executed successfully", result.message)
        assertTrue(result.interpretedCorrectly)
        val printed = outputStream.toString().trim()
        assertEquals("Program 2\n Output: 43", printed)
    }
}
