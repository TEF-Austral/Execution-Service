package parser.factory

import TokenStream
import builder.DefaultNodeBuilder
import builder.NodeBuilder
import parser.ParserInterface
import parser.statement.StatementParser
import parser.statement.expression.ExpressionParser
import type.Version

sealed interface ParserFactory {
    fun createWithVersion(
        version: Version,
        nodeBuilder: NodeBuilder = DefaultNodeBuilder(),
        tokenList: TokenStream,
    ): ParserInterface

    fun createDefault(
        nodeBuilder: NodeBuilder = DefaultNodeBuilder(),
        tokenList: TokenStream,
    ): ParserInterface

    fun createCustomParser(
        nodeBuilder: NodeBuilder,
        tokenList: TokenStream,
        expressionParser: ExpressionParser,
        statementParser: StatementParser,
    ): ParserInterface
}
