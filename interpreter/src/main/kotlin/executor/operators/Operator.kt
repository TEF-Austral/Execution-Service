package executor.operators

import result.InterpreterResult
import variable.Variable

interface Operator {
    fun canHandle(symbol: String): Boolean

    fun operate(
        left: Variable,
        right: Variable,
    ): InterpreterResult
}
