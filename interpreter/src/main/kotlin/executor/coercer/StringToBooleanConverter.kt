package executor.coercer

import result.InterpreterResult
import type.CommonTypes
import variable.Variable

class StringToBooleanConverter : ValueConverter {
    override fun canHandle(targetType: Any): Boolean = targetType == CommonTypes.BOOLEAN

    override fun convert(result: InterpreterResult): Variable? {
        val rawValue = result.interpreter?.getValue().toString()
        return when (rawValue.lowercase()) {
            "true" -> Variable(CommonTypes.BOOLEAN_LITERAL, true)
            "false" -> Variable(CommonTypes.BOOLEAN_LITERAL, false)
            else -> null
        }
    }
}
