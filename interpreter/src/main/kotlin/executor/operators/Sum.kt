package executor.operators

import result.InterpreterResult
import variable.Variable
import type.CommonTypes

object Sum : Operator {
    override fun canHandle(symbol: String): Boolean = symbol == "+"

    override fun operate(
        left: Variable,
        right: Variable,
    ): InterpreterResult {
        val intResult = sumAsInt(left, right)
        if (intResult !=
            null
        ) {
            return InterpreterResult(true, "Program executed successfully", intResult)
        }

        val doubleResult = sumAsDouble(left, right)
        if (doubleResult !=
            null
        ) {
            return InterpreterResult(true, "Program executed successfully", doubleResult)
        }

        val leftStr = left.getValue()?.toString() ?: ""
        val rightStr = right.getValue()?.toString() ?: ""
        return InterpreterResult(
            true,
            "Program executed successfully",
            Variable(
                CommonTypes.STRING_LITERAL,
                leftStr + rightStr,
            ),
        )
    }

    private fun sumAsInt(
        left: Variable,
        right: Variable,
    ): Variable? {
        val leftInt = left.getValue()?.toString()?.toIntOrNull()
        val rightInt = right.getValue()?.toString()?.toIntOrNull()
        return if (leftInt != null && rightInt != null) {
            Variable(CommonTypes.NUMBER_LITERAL, leftInt + rightInt)
        } else {
            null
        }
    }

    private fun sumAsDouble(
        left: Variable,
        right: Variable,
    ): Variable? {
        val leftDouble = left.getValue()?.toString()?.toDoubleOrNull()
        val rightDouble = right.getValue()?.toString()?.toDoubleOrNull()
        return if (leftDouble != null && rightDouble != null) {
            Variable(CommonTypes.NUMBER_LITERAL, leftDouble + rightDouble)
        } else {
            null
        }
    }
}
