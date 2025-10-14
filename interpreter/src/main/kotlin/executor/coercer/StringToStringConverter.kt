package executor.coercer

import result.InterpreterResult
import type.CommonTypes
import variable.Variable

class StringToStringConverter : ValueConverter {
    override fun canHandle(targetType: Any): Boolean = targetType == CommonTypes.STRING

    override fun convert(result: InterpreterResult): Variable {
        val rawValue = result.interpreter?.getValue().toString()
        return Variable(CommonTypes.STRING_LITERAL, rawValue)
    }
}
