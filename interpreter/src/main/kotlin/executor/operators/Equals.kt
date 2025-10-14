package executor.operators

import result.InterpreterResult
import type.CommonTypes
import variable.Variable

object Equals : Operator {
    override fun canHandle(symbol: String): Boolean = symbol == "=="

    override fun operate(
        left: Variable,
        right: Variable,
    ): InterpreterResult {
        if (left.getType() != right.getType()) {
            val resultVar = Variable(CommonTypes.BOOLEAN_LITERAL, "false")
            return InterpreterResult(true, "Success Equals (Different Types)", resultVar)
        }

        val areEqual =
            when (left.getType()) {
                CommonTypes.NUMBER_LITERAL -> compareAsDouble(left, right) ?: false
                CommonTypes.BOOLEAN_LITERAL -> compareAsBoolean(left, right) ?: false
                CommonTypes.STRING_LITERAL -> compareAsString(left, right)
                else -> compareAsString(left, right)
            }

        val resultVar = Variable(CommonTypes.BOOLEAN_LITERAL, areEqual.toString())
        return InterpreterResult(true, "Success Equals (Same Type)", resultVar)
    }

    private fun compareAsDouble(
        left: Variable,
        right: Variable,
    ): Boolean? {
        val leftDouble = left.getValue()?.toString()?.toDoubleOrNull()
        val rightDouble = right.getValue()?.toString()?.toDoubleOrNull()
        return if (leftDouble != null && rightDouble != null) {
            leftDouble == rightDouble
        } else {
            null
        }
    }

    private fun compareAsBoolean(
        left: Variable,
        right: Variable,
    ): Boolean? {
        val leftBool = toBooleanOrNull(left.getValue())
        val rightBool = toBooleanOrNull(right.getValue())
        return if (leftBool != null && rightBool != null) {
            leftBool == rightBool
        } else {
            null
        }
    }

    private fun compareAsString(
        left: Variable,
        right: Variable,
    ): Boolean {
        val leftStr = left.getValue()?.toString() ?: ""
        val rightStr = right.getValue()?.toString() ?: ""
        return leftStr == rightStr
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
