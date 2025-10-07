package executor.operators

import result.InterpreterResult
import variable.Variable
import type.CommonTypes

object Multiplication : Operator {
    override fun canHandle(symbol: String): Boolean = symbol == "*"

    override fun operate(
        left: Variable,
        right: Variable,
    ): InterpreterResult {
        val intResult = multiplyAsInt(left, right)
        if (intResult != null) return InterpreterResult(true, "Succes Multiplication", intResult)

        val doubleResult = multiplyAsDouble(left, right)
        if (doubleResult !=
            null
        ) {
            return InterpreterResult(true, "Succes Multiplication", doubleResult)
        }

        return InterpreterResult(
            false,
            "Type mismatch: Incompatible types for Multiplication operation",
            Variable(CommonTypes.NUMBER_LITERAL, null),
        )
    }

    private fun multiplyAsInt(
        left: Variable,
        right: Variable,
    ): Variable? {
        val leftInt = left.getValue()?.toString()?.toIntOrNull()
        val rightInt = right.getValue()?.toString()?.toIntOrNull()
        return if (leftInt != null && rightInt != null) {
            Variable(CommonTypes.NUMBER_LITERAL, leftInt * rightInt)
        } else {
            null
        }
    }

    private fun multiplyAsDouble(
        left: Variable,
        right: Variable,
    ): Variable? {
        val leftDouble = left.getValue()?.toString()?.toDoubleOrNull()
        val rightDouble = right.getValue()?.toString()?.toDoubleOrNull()
        return if (leftDouble != null && rightDouble != null) {
            Variable(CommonTypes.NUMBER_LITERAL, leftDouble * rightDouble)
        } else {
            null
        }
    }
}
