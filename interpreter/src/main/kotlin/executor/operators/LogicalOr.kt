package executor.operators

import result.InterpreterResult
import type.CommonTypes
import variable.Variable

object LogicalOr : Operator {
    override fun canHandle(symbol: String): Boolean = symbol == "||"

    override fun operate(
        left: Variable,
        right: Variable,
    ): InterpreterResult {
        val leftBool = toBooleanOrNull(left.getValue())
        val rightBool = toBooleanOrNull(right.getValue())

        return if (leftBool != null && rightBool != null) {
            val result = Variable(CommonTypes.BOOLEAN_LITERAL, leftBool || rightBool)
            InterpreterResult(true, "Success LogicalOr", result)
        } else {
            InterpreterResult(
                false,
                "Type mismatch: Incompatible types for LogicalOr operation",
                null,
            )
        }
    }

    private fun toBooleanOrNull(value: Any?): Boolean? {
        val stringValue = value?.toString()
        return when {
            stringValue.equals("true", ignoreCase = true) -> true
            stringValue.equals("false", ignoreCase = true) -> false
            else -> null
        }
    }
}
