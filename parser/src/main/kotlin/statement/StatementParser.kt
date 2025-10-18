package parser.statement

import parser.Parser
import parser.result.StatementErrorResult
import parser.result.StatementResult

class StatementParser(
    private val statementCommands: List<StatementBuilder>,
) {
    fun parse(parser: Parser): StatementResult {
        for (command in statementCommands) {
            val ast = parser.peak()
            if (command.canHandle(ast, parser)) {
                val result = command.parse(parser)
                if (!result.isSuccess()) {
                    continue
                }
                return result
            }
        }
        return StatementErrorResult(parser, "Can't be handled currently by the statement parser")
    }
}
