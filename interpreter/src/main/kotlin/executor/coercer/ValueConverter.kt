package executor.coercer

import result.InterpreterResult
import variable.Variable

sealed interface ValueConverter {
    fun canHandle(targetType: Any): Boolean

    fun convert(result: InterpreterResult): Variable?
}
