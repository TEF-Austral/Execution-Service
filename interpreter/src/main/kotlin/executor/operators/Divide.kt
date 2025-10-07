package executor.operators

import result.InterpreterResult
import variable.Variable
import type.CommonTypes

object Divide : Operator {
    override fun canHandle(symbol: String): Boolean = symbol == "/"

    override fun operate(
        left: Variable,
        right: Variable,
    ): InterpreterResult {
        if (isZeroDivisor(
                right,
            )
        ) {
            return InterpreterResult(
                false,
                "Can't divide by zero",
                Variable(CommonTypes.NUMBER_LITERAL, null),
            )
        }

        val intResult = divideAsInt(left, right)
        if (intResult != null) return InterpreterResult(true, "Success Division", intResult)

        val doubleResult = divideAsDouble(left, right)
        if (doubleResult != null) return InterpreterResult(true, "Success Division", doubleResult)

        return InterpreterResult(
            false,
            "Type mismatch: Incompatible types for division operator",
            Variable(CommonTypes.NUMBER_LITERAL, null),
        )
    }

    private fun divideAsInt(
        left: Variable,
        right: Variable,
    ): Variable? {
        val leftInt = left.getValue()?.toString()?.toIntOrNull()
        val rightInt = right.getValue()?.toString()?.toIntOrNull()
        if (leftInt != null && rightInt != null) {
            if (rightInt == 0) return null
            if (leftInt % rightInt != 0) {
                return Variable(CommonTypes.NUMBER_LITERAL, leftInt.toDouble() / rightInt)
            }
            return Variable(CommonTypes.NUMBER_LITERAL, leftInt / rightInt)
        }
        return null
    }

    private fun divideAsDouble(
        left: Variable,
        right: Variable,
    ): Variable? {
        val leftDouble = left.getValue()?.toString()?.toDoubleOrNull()
        val rightDouble = right.getValue()?.toString()?.toDoubleOrNull()
        if (leftDouble != null && rightDouble != null) {
            if (rightDouble == 0.0) return null
            return Variable(CommonTypes.NUMBER_LITERAL, leftDouble / rightDouble)
        }
        return null
    }

    private fun isZeroDivisor(right: Variable): Boolean {
        val rightValueAsDouble = right.getValue()?.toString()?.toDoubleOrNull()
        return rightValueAsDouble == 0.0
    }
}
