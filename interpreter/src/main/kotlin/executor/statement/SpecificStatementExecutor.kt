package executor.statement

import node.Statement

interface SpecificStatementExecutor : StatementExecutor {
    fun canHandle(statement: Statement): Boolean
}
