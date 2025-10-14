package executor.operators

import result.InterpreterResult
import variable.Variable
import type.CommonTypes

object Subtraction : Operator {
    override fun canHandle(symbol: String): Boolean = symbol == "-"

    override fun operate(
        left: Variable,
        right: Variable,
    ): InterpreterResult {
        val intResult = subtractAsInt(left, right)
        if (intResult != null) return InterpreterResult(true, "Success Substraction", intResult)

        val doubleResult = subtractAsDouble(left, right)
        if (doubleResult !=
            null
        ) {
            return InterpreterResult(true, "Success Substraction", doubleResult)
        }

        return InterpreterResult(
            false,
            "Type mismatch: Incompatible types for Substraction operation",
            Variable(CommonTypes.STRING_LITERAL, null),
        )
    }

    private fun subtractAsInt(
        left: Variable,
        right: Variable,
    ): Variable? {
        val leftInt = left.getValue()?.toString()?.toIntOrNull()
        val rightInt = right.getValue()?.toString()?.toIntOrNull()
        return if (leftInt != null && rightInt != null) {
            Variable(CommonTypes.NUMBER_LITERAL, leftInt - rightInt)
        } else {
            null
        }
    }

    private fun subtractAsDouble(
        left: Variable,
        right: Variable,
    ): Variable? {
        val leftDouble = left.getValue()?.toString()?.toDoubleOrNull()
        val rightDouble = right.getValue()?.toString()?.toDoubleOrNull()
        return if (leftDouble != null && rightDouble != null) {
            Variable(CommonTypes.NUMBER_LITERAL, leftDouble - rightDouble)
        } else {
            null
        }
    }
}
