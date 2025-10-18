package parser.statement

import Token
import parser.Parser
import parser.result.StatementResult

sealed interface StatementBuilder {
    fun parse(parser: Parser): StatementResult

    fun canHandle(
        token: Token?,
        parser: Parser,
    ): Boolean
}
