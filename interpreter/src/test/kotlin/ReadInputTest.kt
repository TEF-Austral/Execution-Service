import coordinates.Position
import factory.InterpreterFactoryVersionOnePointOne
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import type.CommonTypes
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.PrintStream
import node.BinaryExpression
import node.DeclarationStatement
import node.IdentifierExpression
import node.LiteralExpression
import node.PrintStatement
import node.Program
import node.ReadInputExpression
import stream.MockAstStream

class ReadInputTest {
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
    fun `should read a string input and print it`() {
        val userInput = "John Doe\n"
        setInput(userInput)

        // Program: let name: string = readInput("Enter name: "); println(name);
        val program =
            Program(
                statements =
                    listOf(
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "name",
                                    Position(1, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.STRING,
                                    "string",
                                    Position(1, 11),
                                ),
                            initialValue =
                                ReadInputExpression(
                                    printValue =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.STRING_LITERAL,
                                                "Enter name: ",
                                                Position(1, 20),
                                            ),
                                            Position(1, 20),
                                        ),
                                    coordinates = Position(1, 20),
                                ),
                            Position(1, 1),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "name",
                                        Position(2, 9),
                                    ),
                                    Position(2, 9),
                                ),
                            Position(2, 1),
                        ),
                    ),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)

        assertTrue(result.interpretedCorrectly, "Interpreter should have run successfully.")

        val expectedOutput =
            "Enter name: John Doe"
        assertEquals(expectedOutput, outContent.toString().trim().replace("\r\n", "\n"))
    }

    @Test
    fun `should read a number input, perform operation and print it`() {
        val userInput = "25\n"
        setInput(userInput)

        // Program: let age: number = readInput("Enter age: ");
        // let newAge: number = age + 1;
        // println(newAge);
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
                        DeclarationStatement(
                            PrintScriptToken(CommonTypes.LET, "let", Position(2, 1)),
                            identifier =
                                PrintScriptToken(
                                    CommonTypes.IDENTIFIER,
                                    "newAge",
                                    Position(2, 5),
                                ),
                            dataType =
                                PrintScriptToken(
                                    CommonTypes.NUMBER,
                                    "number",
                                    Position(2, 13),
                                ),
                            initialValue =
                                BinaryExpression(
                                    left =
                                        IdentifierExpression(
                                            PrintScriptToken(
                                                CommonTypes.IDENTIFIER,
                                                "age",
                                                Position(2, 22),
                                            ),
                                            Position(2, 22),
                                        ),
                                    operator =
                                        PrintScriptToken(
                                            CommonTypes.OPERATORS,
                                            "+",
                                            Position(2, 26),
                                        ),
                                    right =
                                        LiteralExpression(
                                            PrintScriptToken(
                                                CommonTypes.NUMBER_LITERAL,
                                                "1",
                                                Position(2, 28),
                                            ),
                                            Position(2, 28),
                                        ),
                                    Position(2, 22),
                                ),
                            Position(2, 1),
                        ),
                        PrintStatement(
                            expression =
                                IdentifierExpression(
                                    PrintScriptToken(
                                        CommonTypes.IDENTIFIER,
                                        "newAge",
                                        Position(3, 9),
                                    ),
                                    Position(3, 9),
                                ),
                            Position(3, 1),
                        ),
                    ),
            )

        val interpreter = InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        val mockAstStream = MockAstStream(program)
        val result = interpreter.interpret(mockAstStream)

        assertTrue(result.interpretedCorrectly)

        val expectedOutput =
            "Enter age: 26"
        assertEquals(expectedOutput, outContent.toString().trim().replace("\r\n", "\n"))
    }
}
