import result.InterpreterResult
import stream.AstStream

interface Interpreter {
    fun interpret(stream: AstStream): InterpreterResult
}
