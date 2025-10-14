import coordinates.Position
import emitter.Emitter
import factory.DefaultInterpreterFactory.createWithVersionAndEmitterAndInputProvider
import input.TerminalInputProvider
import node.LiteralExpression
import node.PrintStatement
import node.Program
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import result.InterpreterResult
import stream.MockAstStream
import type.CommonTypes
import type.Version

class FactoryTest {

    private val literalStringExpression =
        LiteralExpression(
            PrintScriptToken(CommonTypes.STRING_LITERAL, "\"hola\"", Position(1, 1)),
            Position(1, 1),
        )

    private class TestEmitter : Emitter {
        val messages = mutableListOf<String>()

        override fun emit(value: InterpreterResult) {
            messages.add(value.message)
        }

        override fun stringEmit(value: String) {
            messages.add(value)
        }
    }

    @Test
    fun `createWithVersionAndEmitter for version 1_0 should create a working interpreter`() {
        val emitter = TestEmitter()
        val interpreter =
            createWithVersionAndEmitterAndInputProvider(
                Version.VERSION_1_0,
                emitter,
                TerminalInputProvider(),
            )
        val printStatement = PrintStatement(literalStringExpression, Position(1, 1))
        val program = Program(listOf(printStatement))
        val mockAstStream = MockAstStream(program)

        val result = interpreter.interpret(mockAstStream)

        assertTrue(result.interpretedCorrectly)
        assertEquals(1, emitter.messages.size)
        assertEquals("Print executed successfully", emitter.messages[0])
    }

    @Test
    fun `createWithVersionAndEmitter for version 1_1 should create a working interpreter`() {
        val emitter = TestEmitter()
        val interpreter =
            createWithVersionAndEmitterAndInputProvider(
                Version.VERSION_1_1,
                emitter,
                TerminalInputProvider(),
            )
        val printStatement = PrintStatement(literalStringExpression, Position(1, 1))
        val program = Program(listOf(printStatement))

        val mockAstStream = MockAstStream(program)

        val result = interpreter.interpret(mockAstStream)

        assertTrue(result.interpretedCorrectly)
        assertEquals(1, emitter.messages.size)
        assertEquals("Print executed successfully", emitter.messages[0])
    }
}
