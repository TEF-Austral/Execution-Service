package parser.result

import node.Statement

sealed interface StatementResult : ParserResult {
    fun getStatement(): Statement
}
