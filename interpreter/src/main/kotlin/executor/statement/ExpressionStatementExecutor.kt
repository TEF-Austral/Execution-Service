package executor.statement

import data.DataBase
import executor.expression.DefaultExpressionExecutor
import result.InterpreterResult
import node.ExpressionStatement
import node.Statement

class ExpressionStatementExecutor(
    private val defaultExpressionExecutor: DefaultExpressionExecutor,
) : SpecificStatementExecutor {
    override fun canHandle(statement: Statement): Boolean = statement is ExpressionStatement

    override fun execute(
        statement: Statement,
        database: DataBase,
    ): InterpreterResult {
        return try {
            val expressionStatement = statement as ExpressionStatement
            val expressionResult =
                defaultExpressionExecutor.execute(
                    expressionStatement.getExpression(),
                    database,
                )
            if (!expressionResult.interpretedCorrectly) {
                return expressionResult
            }
            InterpreterResult(true, "Expression executed successfully", null)
        } catch (e: Exception) {
            InterpreterResult(false, "Error executing expression statement: ${e.message}", null)
        }
    }
}
