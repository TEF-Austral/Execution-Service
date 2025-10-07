package executor.coercer

import result.InterpreterResult
import type.CommonTypes
import variable.Variable

class StringToNumberConverter : ValueConverter {
    override fun canHandle(targetType: Any): Boolean = targetType == CommonTypes.NUMBER

    override fun convert(result: InterpreterResult): Variable? {
        val rawValue = result.interpreter?.getValue().toString()
        return if (rawValue.contains('.')) {
            rawValue.toDoubleOrNull()?.let { doubleValue ->
                Variable(CommonTypes.NUMBER, doubleValue)
            }
        } else {
            rawValue.toIntOrNull()?.let { intValue ->
                Variable(CommonTypes.NUMBER, intValue)
            }
        }
    }
}
