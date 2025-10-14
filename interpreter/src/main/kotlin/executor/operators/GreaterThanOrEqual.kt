package executor.operators

import result.InterpreterResult
import variable.Variable
import type.CommonTypes

object GreaterThanOrEqual : Operator {
    override fun canHandle(symbol: String): Boolean = symbol == ">="

    override fun operate(
        left: Variable,
        right: Variable,
    ): InterpreterResult {
        val numericResult = compareAsDouble(left, right)
        if (numericResult != null) {
            val resultVar = Variable(CommonTypes.BOOLEAN_LITERAL, numericResult)
            return InterpreterResult(true, "Success GreaterThanOrEqual (Numeric)", resultVar)
        }

        val booleanResult = compareAsBoolean(left, right)
        if (booleanResult != null) {
            val resultVar = Variable(CommonTypes.BOOLEAN_LITERAL, booleanResult)
            return InterpreterResult(true, "Success GreaterThanOrEqual (boolean)", resultVar)
        }

        val stringResult = compareIfBothAreStrings(left, right)
        if (stringResult != null) {
            val resultVar = Variable(CommonTypes.BOOLEAN_LITERAL, stringResult)
            return InterpreterResult(true, "Success GreaterThanOrEqual (String)", resultVar)
        }

        return InterpreterResult(
            false,
            "Type mismatch: Incompatible types for >= operation between ${left.getType()} and ${right.getType()}.",
            null,
        )
    }

    private fun compareAsDouble(
        left: Variable,
        right: Variable,
    ): Boolean? {
        val leftDouble = left.getValue()?.toString()?.toDoubleOrNull()
        val rightDouble = right.getValue()?.toString()?.toDoubleOrNull()
        return if (leftDouble != null && rightDouble != null) {
            leftDouble >= rightDouble
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
            leftBool == rightBool || leftBool
        } else {
            null
        }
    }

    private fun compareIfBothAreStrings(
        left: Variable,
        right: Variable,
    ): Boolean? {
        if (left.getType() == CommonTypes.STRING_LITERAL &&
            right.getType() == CommonTypes.STRING_LITERAL
        ) {
            val leftStr = left.getValue()?.toString() ?: ""
            val rightStr = right.getValue()?.toString() ?: ""
            return leftStr >= rightStr
        }
        return null
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
