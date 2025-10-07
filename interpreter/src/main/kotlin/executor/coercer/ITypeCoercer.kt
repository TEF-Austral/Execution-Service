package executor.coercer

import result.InterpreterResult

interface ITypeCoercer {
    fun coerce(
        rawValue: InterpreterResult,
        targetType: Any,
    ): InterpreterResult
}
