package executor.statement

import data.DataBase
import result.InterpreterResult
import node.Statement

sealed interface StatementExecutor {
    fun execute(
        statement: Statement,
        database: DataBase,
    ): InterpreterResult
}
