package executor.coercer

import result.InterpreterResult

class TypeCoercer(
    private val converters: List<ValueConverter>,
) : ITypeCoercer {

    override fun coerce(
        rawValue: InterpreterResult,
        targetType: Any,
    ): InterpreterResult {
        for (converter in converters) {
            if (converter.canHandle(targetType)) {
                return handleConversion(converter, rawValue, targetType)
            }
        }
        return InterpreterResult(false, "No conversion available for type '$targetType'", null)
    }

    private fun handleConversion(
        converter: ValueConverter,
        rawValue: InterpreterResult,
        targetType: Any,
    ): InterpreterResult {
        val convertedVariable = converter.convert(rawValue)
        return if (convertedVariable != null) {
            InterpreterResult(true, "Coercion successful", convertedVariable)
        } else {
            InterpreterResult(
                false,
                "Failed to convert value '${rawValue.interpreter?.getValue()}' to type '$targetType'",
                null,
            )
        }
    }
}
