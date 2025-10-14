package executor.expression

import data.DataBase
import executor.operators.Divide
import executor.operators.Equals
import executor.operators.GraterThan
import executor.operators.GreaterThanOrEqual
import executor.operators.LessThan
import executor.operators.LessThanOrEqual
import executor.operators.LogicalAnd
import executor.operators.LogicalOr
import executor.operators.Multiplication
import executor.operators.Operator
import executor.operators.Subtraction
import executor.operators.Sum
import result.InterpreterResult
import node.BinaryExpression
import node.Expression
import type.CommonTypes
import variable.Variable

class BinaryExpressionExecutor(
    private val operators: List<Operator> =
        listOf(
            Sum,
            Divide,
            Multiplication,
            Subtraction,
            LogicalAnd,
            LogicalOr,
            Equals,
            GraterThan,
            GreaterThanOrEqual,
            LessThanOrEqual,
            LessThan,
        ),
    private val expressions: List<SpecificExpressionExecutor>,
) : SpecificExpressionExecutor {
    override fun canHandle(expression: Expression): Boolean = expression is BinaryExpression

    override fun execute(
        expression: Expression,
        database: DataBase,
    ): InterpreterResult =
        try {
            val binaryExpr = expression as BinaryExpression
            executeBinaryExpression(binaryExpr, database)
        } catch (e: Exception) {
            createErrorResult("Error executing binary expression: ${e.message}")
        }

    private fun executeBinaryExpression(
        expression: BinaryExpression,
        database: DataBase,
    ): InterpreterResult {
        val leftResult = executeSubExpression(expression.getLeft(), database)
        if (!leftResult.interpretedCorrectly) return leftResult

        val rightResult = executeSubExpression(expression.getRight(), database)
        if (!rightResult.interpretedCorrectly) return rightResult

        return evaluateOperation(leftResult, rightResult, expression.getOperator())
    }

    private fun executeSubExpression(
        expression: Expression,
        database: DataBase,
    ): InterpreterResult {
        val executor = findExecutorFor(expression)
        return executor?.execute(expression, database)
            ?: createErrorResult("No executor found for expression type")
    }

    private fun findExecutorFor(expression: Expression): SpecificExpressionExecutor? {
        if (expression is BinaryExpression) return this
        return expressions.find { it.canHandle(expression) }
    }

    private fun evaluateOperation(
        leftResult: InterpreterResult,
        rightResult: InterpreterResult,
        operatorValue: String,
    ): InterpreterResult {
        val left = leftResult.interpreter
        val right = rightResult.interpreter
        if (left == null || right == null) {
            return createErrorResult("Operands must be variables")
        }
        val operationResult = performOperation(left, operatorValue, right)

        return if (operationResult.interpretedCorrectly) {
            InterpreterResult(true, "Binary expression evaluated", operationResult.interpreter)
        } else {
            createErrorResult(operationResult.message)
        }
    }

    private fun performOperation(
        left: Variable,
        operatorValue: String,
        right: Variable,
    ): InterpreterResult {
        val operator = operators.find { it.canHandle(operatorValue) }
        return operator?.operate(left, right)
            ?: InterpreterResult(
                false,
                "Can not Support That Operation",
                Variable(CommonTypes.STRING_LITERAL, null),
            )
    }

    private fun createErrorResult(message: String): InterpreterResult =
        InterpreterResult(false, message, null)
}
