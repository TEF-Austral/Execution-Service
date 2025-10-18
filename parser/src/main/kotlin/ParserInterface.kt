package parser

import Token
import builder.NodeBuilder
import parser.result.FinalResult
import parser.result.NextResult
import parser.statement.StatementParser
import parser.statement.expression.ExpressionParser

interface ParserInterface {
    fun parse(): FinalResult

    fun getExpressionParser(): ExpressionParser

    fun getNodeBuilder(): NodeBuilder

    fun getStatementParser(): StatementParser

    fun hasNext(): Boolean

    fun next(): NextResult

    fun peak(): Token?

    fun isAtEnd(): Boolean
}
