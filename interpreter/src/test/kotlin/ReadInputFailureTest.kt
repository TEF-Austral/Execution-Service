import coordinates.Position
import factory.InterpreterFactoryVersionOnePointOne
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.PrintStream
import node.DeclarationStatement
import node.LiteralExpression
import node.Program
import node.ReadInputExpression
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import stream.MockAstStream
import type.CommonTypes

class ReadInputFailureTest {
    private val originalIn: InputStream = System.`in`
    private val originalOut: PrintStream = System.out
    private lateinit var outContent: ByteArrayOutputStream

    @BeforeEach
    fun setUpStreams() {
        outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))
    }

    @AfterEach
    fun restoreStreams() {
        System.setIn(originalIn)
        System.setOut(originalOut)
    }

    private fun setInput(input: String) {
        System.setIn(ByteArrayInputStream(input.toByteArray()))
    }

    @Test
    fun `should fail when reading a string into a number variable`() {
        val userInput = "not a number\n"
        setInput(userInput)

        // Program: let age: number = readInput("Enter age: ");
        val program =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "age",
                                    Position(1, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(1, 10),
                                ),
                            initialValue =
                                ReadInputExpression(
                                    printValue =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.STRING_LITERAL,
                                                "Enter age: ",
                                                Position(1, 19),
                                            ),
                                            Position(1, 19),
                                        ),
                                    coordinates = Position(1, 19),
                                ),
                            Position(1, 1),
                        ),
                    ),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)

        val result = interpreter.interpret(mockAstStream)

        assertFalse(
            result.interpretedCorrectly,
            "Interpreter should have failed due to type mismatch.",
        )
        assertEquals("Failed to convert value 'not a number' to type 'number'", result.message)
    }

    @Test
    fun `should fail when reading a non-boolean string into a boolean variable`() {
        val userInput = "yes\n"
        setInput(userInput)

        // Program: let isMember: Boolean = readInput("Is member? ");
        val program =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "isMember",
                                    Position(1, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.BOOLEAN,
                                    "boolean",
                                    Position(1, 15),
                                ),
                            initialValue =
                                ReadInputExpression(
                                    printValue =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.STRING_LITERAL,
                                                "Is member? ",
                                                Position(1, 25),
                                            ),
                                            Position(1, 25),
                                        ),
                                    coordinates = Position(1, 25),
                                ),
                            Position(1, 1),
                        ),
                    ),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)

        assertFalse(result.interpretedCorrectly)
        assertEquals("Failed to convert value 'yes' to type 'boolean'", result.message)
    }
}
