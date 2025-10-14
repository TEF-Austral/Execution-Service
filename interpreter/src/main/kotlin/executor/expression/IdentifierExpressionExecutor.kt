package executor.expression

import data.DataBase
import node.Expression
import node.IdentifierExpression
import result.InterpreterResult
import variable.Variable

class IdentifierExpressionExecutor : SpecificExpressionExecutor {
    override fun canHandle(expression: Expression): Boolean = expression is IdentifierExpression

    override fun execute(
        expression: Expression,
        database: DataBase,
    ): InterpreterResult =
        try {
            val identifierExpression = expression as IdentifierExpression
            val identifier = identifierExpression.getValue()

            val variable = database.getValue(identifier)

            if (variable == null) {
                InterpreterResult(false, "Identifier '$identifier' not found", null)
            } else {
                InterpreterResult(true, "Identifier evaluated", variable as Variable)
            }
        } catch (e: Exception) {
            InterpreterResult(false, "Error executing identifier expression: ${e.message}", null)
        }
}
