package executor.statement

import data.DataBase
import executor.expression.DefaultExpressionExecutor
import node.DeclarationStatement
import node.Statement
import result.InterpreterResult

class DeclarationStatementExecutor(
    private val defaultExpressionExecutor: DefaultExpressionExecutor,
    private val declarationStatementExecutor: List<SpecificStatementExecutor>,
) : SpecificStatementExecutor {
    override fun canHandle(statement: Statement): Boolean = statement is DeclarationStatement

    override fun execute(
        statement: Statement,
        database: DataBase,
    ): InterpreterResult {
        return try {
            for (executor in declarationStatementExecutor) {
                if (executor.canHandle(statement)) {
                    return executor.execute(statement, database)
                }
            }
            return InterpreterResult(false, "No Declaration found for the given statement", null)
        } catch (e: Exception) {
            InterpreterResult(false, "Error executing declaration statement: ${e.message}", null)
        }
    }
}
