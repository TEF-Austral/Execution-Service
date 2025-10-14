import coordinates.Position
import result.InterpreterResult
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
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import stream.MockAstStream
import type.CommonTypes

class InterpreterFailureTest {
    @Test
    fun `Undefined Variable Usage Should Fail`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val case =
            Program(
                statements =
                    listOf(
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "undefinedVar",
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

        assertFalse(result.interpretedCorrectly)
        assertTrue(
            result.message.contains("undefinedVar") ||
                result.message.contains("undefined") ||
                result.message.contains(
                    "not found",
                ),
        )
    }

    @Test
    fun `Assignment to Undeclared Variable Should Fail`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val case =
            Program(
                statements =
                    listOf(
                        AssignmentStatement(
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "undeclaredVar",
                                    Position(0, 0),
                                ),
                            value =
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
                    ),
            )
        val mockAstStream = MockAstStream(case)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertFalse(result.interpretedCorrectly)
        assertEquals("Error: Variable 'undeclaredVar' not declared", result.message)
    }

    @Test
    fun `Type Mismatch in Assignment Should Fail`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val case =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "numberVar",
                                    Position(0, 0),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(0, 0),
                                ),
                            initialValue = null,
                            Position(0, 0),
                        ),
                        AssignmentStatement(
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "numberVar",
                                    Position(0, 0),
                                ),
                            value =
                                LiteralExpression(
                                    PrintScriptToken(
                                        CommonTypes.STRING_LITERAL,
                                        "not a number",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(case)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertFalse(result.interpretedCorrectly)
        assertTrue(
            result.message.contains("type") ||
                result.message.contains("mismatch") ||
                result.message.contains("incompatible"),
        )
    }

    @Test
    fun `Invalid Arithmetic Operation Should Fail`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val case =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "text1",
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
                                        "hello",
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
                                    "text2",
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
                                        "world",
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
                                                "text1",
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
                                                "text2",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(case)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertFalse(result.interpretedCorrectly)
        assertEquals(
            "Type mismatch: Incompatible types for Multiplication operation",
            result.message,
        )
    }

    @Test
    fun `String Subtraction Should Fail`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val case =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "str1",
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
                                        "hello",
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
                                    "str2",
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
                                        "world",
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
                                                "str1",
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
                                                "str2",
                                                Position(0, 0),
                                            ),
                                            Position(0, 0),
                                        ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )
        val mockAstStream = MockAstStream(case)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertFalse(result.interpretedCorrectly)
        assertEquals("Type mismatch: Incompatible types for Substraction operation", result.message)
    }

    @Test
    fun `String Division Should Fail`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val case =
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
                                        "hello",
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
                                        "2",
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
                                            "/",
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
                    ),
            )
        val mockAstStream = MockAstStream(case)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertFalse(result.interpretedCorrectly)
        assertEquals("Type mismatch: Incompatible types for division operator", result.message)
    }

    @Test
    fun `Invalid Declaration Program`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val programWithInvalidDeclaration =
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
                                        CommonTypes.STRING_LITERAL,
                                        "hola",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )

        val mockAstStream = MockAstStream(programWithInvalidDeclaration)
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()

        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertFalse(
            result.interpretedCorrectly,
            "La interpretaciÃ³n deberÃ­a fallar por un error de tipos.",
        )

        val errorMessage = result.message.lowercase()
        assertTrue(
            errorMessage.contains("type") ||
                errorMessage.contains("mismatch") ||
                errorMessage.contains("incompatible"),
            "El mensaje de error deberÃ­a indicar un problema de tipos. Mensaje recibido: '${result.message}'",
        )
    }

    @Test
    fun `Printing Undefined Variable Should Fail`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val programWithUndefinedVar =
            Program(
                statements =
                    listOf(
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "undeclaredVar",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(programWithUndefinedVar)

        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertFalse(
            result.interpretedCorrectly,
            "El programa deberÃ­a fallar al usar una variable no definida.",
        )

        val errorMessage = result.message.lowercase()
        assertTrue(
            errorMessage.contains("undefined") ||
                errorMessage.contains("not found") ||
                errorMessage.contains("not declared"),
            "El mensaje de error deberÃ­a indicar que la variable no fue encontrada. Mensaje: '${result.message}'",
        )
    }

    @Test
    fun `Multiplying String By number Should Fail`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val programWithInvalidMultiplication =
            Program(
                statements =
                    listOf(
                        PrintStatement(
                            expression =
                                BinaryExpression(
                                    left =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.STRING_LITERAL,
                                                "hola",
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
                                                "5",
                                                Position(0, 0),
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
        val mockAstStream = MockAstStream(programWithInvalidMultiplication)

        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertFalse(
            result.interpretedCorrectly,
            "El programa deberÃ­a fallar al multiplicar un string y un nÃºmero.",
        )

        assertEquals(
            "Type mismatch: Incompatible types for Multiplication operation",
            result.message,
        )
    }

    @Test
    fun `Dividing String By number Should Fail`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val programWithInvalidDivision =
            Program(
                statements =
                    listOf(
                        PrintStatement(
                            expression =
                                BinaryExpression(
                                    left =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.STRING_LITERAL,
                                                "hola",
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
                                    // El operador ahora es '/'
                                    right =
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
                            Position(0, 0),
                        ),
                    ),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()

        val mockAstStream = MockAstStream(programWithInvalidDivision)

        val result: InterpreterResult = interpreter.interpret(mockAstStream)

        assertFalse(
            result.interpretedCorrectly,
            "El programa deberÃ­a fallar al dividir un string por un nÃºmero.",
        )

        val errorMessage = result.message.lowercase()
        print(errorMessage)
        assertEquals("Type mismatch: Incompatible types for division operator", result.message)
    }

    @Test
    fun `Division by Zero Edge Case Should Fail`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        print("Program 17\n Output: ")
        val case17 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(0, 0)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "numerator",
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
                                    "denominator",
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
                                    "result",
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
                                                "numerator",
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
                                                "denominator",
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
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(case17)
        val result: InterpreterResult = interpreter.interpret(mockAstStream)
        assertFalse(result.interpretedCorrectly)
        assertEquals("Can't divide by zero", result.message)
        val printed = outputStream.toString().trim()
        assertEquals("Program 17\n Output:", printed)
    }

    @Test
    fun `Const Assigment With No Value Should Fail`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        print("Program 3\n Output: ")
        val case2 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.CONST, "const", Position(0, 0)),
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
                            initialValue = null,
                            Position(0, 0),
                        ),
                        AssignmentStatement(
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "x",
                                    Position(0, 0),
                                ),
                            value =
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
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "x",
                                        Position(0, 0),
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
        assertFalse(result.interpretedCorrectly)
        assertEquals("Error: Constant 'x' must be initialized with a value.", result.message)
    }

    @Test
    fun `Const Assigment And Reasigment Value`() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        print("Program 3\n Output: ")
        val case2 =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.CONST, "const", Position(0, 0)),
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
                                        "42",
                                        Position(0, 0),
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
                                    Position(0, 0),
                                ),
                            value =
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
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "x",
                                        Position(0, 0),
                                    ),
                                    Position(0, 0),
                                ),
                            Position(0, 0),
                        ),
                    ),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val result: InterpreterResult = interpreter.interpret(MockAstStream(case2))
        assertFalse(result.interpretedCorrectly)
        assertEquals("Error: Cannot reassign a value to a constant 'x'", result.message)
    }
}
