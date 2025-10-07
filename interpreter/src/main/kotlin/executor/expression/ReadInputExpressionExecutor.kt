package executor.expression

import data.DataBase
import input.InputProvider
import node.Expression
import node.LiteralExpression
import node.ReadInputExpression
import result.InterpreterResult

class ReadInputExpressionExecutor(
    private val inputProvider: InputProvider,
) : SpecificExpressionExecutor {
    override fun canHandle(expression: Expression): Boolean = expression is ReadInputExpression

    override fun execute(
        expression: Expression,
        database: DataBase,
    ): InterpreterResult {
        val readInputExpression = expression as ReadInputExpression

        val value = readInputExpression.printValue() as LiteralExpression

        val userInput = inputProvider.input(value.getValue())

        return userInput
    }
}
