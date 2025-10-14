import coordinates.Position
import factory.InterpreterFactoryVersionOnePointOne
import node.BinaryExpression
import node.LiteralExpression
import node.PrintStatement
import node.Program
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import type.CommonTypes
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import stream.MockAstStream

class ComparisonOperatorTest {
    @Test
    fun `GreaterThan with numbers should return true when left is greater`() {
        // println(10 > 5);
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
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
                                                "10",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            ">",
                                            Position(1, 12),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "5",
                                                Position(1, 14),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("true", outputStream.toString().trim())
    }

    @Test
    fun `GreaterThan with booleans should return true for 'true greater than false'`() {
        // println(true > false);
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
            Program(
                statements =
                    listOf(
                        PrintStatement(
                            expression =
                                BinaryExpression(
                                    left =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "true",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            ">",
                                            Position(1, 14),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "false",
                                                Position(1, 16),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("true", outputStream.toString().trim())
    }

    @Test
    fun `GreaterThan with strings should return true when left is lexicographically greater`() {
        // println("z" > "a");
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
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
                                                "z",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            ">",
                                            Position(1, 13),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.STRING_LITERAL,
                                                "a",
                                                Position(1, 15),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("true", outputStream.toString().trim())
    }

    @Test
    fun `GreaterThan should fail with mixed incompatible types`() {
        // println(100 > "hello");
        val program =
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
                                                "100",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            ">",
                                            Position(1, 13),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.STRING_LITERAL,
                                                "hello",
                                                Position(1, 15),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)
        assertFalse(result.interpretedCorrectly)
        assertEquals(
            "Type mismatch: Incompatible types for > operation between NUMBER_LITERAL and STRING_LITERAL.",
            result.message,
        )
    }

    @Test
    fun `LessThan with numbers should return true when left is smaller`() {
        // println(3 < 8);
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
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
                                                "3",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "<",
                                            Position(1, 11),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "8",
                                                Position(1, 13),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("true", outputStream.toString().trim())
    }

    @Test
    fun `LessThan with booleans should return true for 'false less than true'`() {
        // println(false < true);
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
            Program(
                statements =
                    listOf(
                        PrintStatement(
                            expression =
                                BinaryExpression(
                                    left =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "false",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "<",
                                            Position(1, 15),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "true",
                                                Position(1, 17),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("true", outputStream.toString().trim())
    }

    @Test
    fun `LessThan should fail with mixed incompatible types`() {
        // println(false < "hello");
        val program =
            Program(
                statements =
                    listOf(
                        PrintStatement(
                            expression =
                                BinaryExpression(
                                    left =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "false",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "<",
                                            Position(1, 15),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.STRING_LITERAL,
                                                "hello",
                                                Position(1, 17),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)
        assertFalse(result.interpretedCorrectly)
        assertEquals(
            "Type mismatch: Incompatible types for < operation between BOOLEAN_LITERAL and STRING_LITERAL.",
            result.message,
        )
    }

    @Test
    fun `GreaterThanOrEqual with numbers should return true for equal values`() {
        // println(10 >= 10);
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
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
                                                "10",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            ">=",
                                            Position(1, 12),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "10",
                                                Position(1, 15),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("true", outputStream.toString().trim())
    }

    @Test
    fun `GreaterThanOrEqual with booleans should return true for 'true greater or equal false'`() {
        // println(true >= false);
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
            Program(
                statements =
                    listOf(
                        PrintStatement(
                            expression =
                                BinaryExpression(
                                    left =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "true",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            ">=",
                                            Position(1, 14),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "false",
                                                Position(1, 17),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("true", outputStream.toString().trim())
    }

    @Test
    fun `LessThanOrEqual with numbers should return true for equal values`() {
        // println(5 <= 5);
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
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
                                            "<=",
                                            Position(1, 11),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "5",
                                                Position(1, 14),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("true", outputStream.toString().trim())
    }

    @Test
    fun `LessThanOrEqual with booleans should return true for 'false less or equal true'`() {
        // println(false <= true);
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
            Program(
                statements =
                    listOf(
                        PrintStatement(
                            expression =
                                BinaryExpression(
                                    left =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "false",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "<=",
                                            Position(1, 15),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "true",
                                                Position(1, 18),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("true", outputStream.toString().trim())
    }

    @Test
    fun `Equals with numbers should return true for equal values`() {
        // println(7.0 == 7);
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
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
                                                "7.0",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "==",
                                            Position(1, 13),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "7",
                                                Position(1, 16),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("true", outputStream.toString().trim())
    }

    @Test
    fun `Equals with booleans should return false for different values`() {
        // println(true == false);
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
            Program(
                statements =
                    listOf(
                        PrintStatement(
                            expression =
                                BinaryExpression(
                                    left =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "true",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "==",
                                            Position(1, 14),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.BOOLEAN_LITERAL,
                                                "false",
                                                Position(1, 17),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("false", outputStream.toString().trim())
    }

    @Test
    fun `Equals with strings should return true for equal values`() {
        // println("test" == "test");
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        val program =
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
                                                "test",
                                                Position(1, 9),
                                            ),
                                            Position(0, 0),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "==",
                                            Position(1, 15),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.STRING_LITERAL,
                                                "test",
                                                Position(1, 18),
                                            ),
                                            Position(0, 0),
                                        ),
                                    coordinates = Position(0, 0),
                                ),
                            coordinates = Position(0, 0),
                        ),
                    ),
                coordinates = Position(0, 0),
            )
        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)
        assertTrue(result.interpretedCorrectly)
        assertEquals("true", outputStream.toString().trim())
    }
}
