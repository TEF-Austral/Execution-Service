package executor.statement

import data.DataBase
import emitter.Emitter
import executor.expression.DefaultExpressionExecutor
import result.InterpreterResult
import node.PrintStatement
import node.Statement

class PrintStatementExecutor(
    private val defaultExpressionExecutor: DefaultExpressionExecutor,
    private val emitter: Emitter,
) : SpecificStatementExecutor {
    override fun canHandle(statement: Statement): Boolean = statement is PrintStatement

    override fun execute(
        statement: Statement,
        database: DataBase,
    ): InterpreterResult {
        try {
            val printStatement = statement as PrintStatement
            val expressionResult =
                defaultExpressionExecutor.execute(
                    printStatement.getExpression(),
                    database,
                )
            if (!expressionResult.interpretedCorrectly) {
                return expressionResult
            }

            val value =
                expressionResult.interpreter ?: return InterpreterResult(
                    false,
                    "Error: No value to print",
                    null,
                )

            val result =
                InterpreterResult(
                    interpretedCorrectly = true,
                    message = "Print executed successfully",
                    interpreter = value,
                )
            emitter.emit(result)
            return result
        } catch (e: Exception) {
            return InterpreterResult(false, "Error executing print statement: ${e.message}", null)
        }
    }
}
