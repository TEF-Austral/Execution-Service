package executor.statement

import data.DataBase
import result.InterpreterResult
import node.Statement

class DefaultStatementExecutor(
    private val specificExecutors: List<SpecificStatementExecutor>,
) : StatementExecutor {
    override fun execute(
        statement: Statement,
        database: DataBase,
    ): InterpreterResult {
        for (executor in specificExecutors) {
            if (executor.canHandle(statement)) {
                return executor.execute(statement, database)
            }
        }
        return InterpreterResult(false, "No executor found for the given statement", null)
    }
}
