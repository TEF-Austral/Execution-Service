package parser.factory

import TokenStream
import builder.NodeBuilder
import parser.Parser
import parser.ParserInterface
import parser.statement.StatementParser
import parser.statement.expression.ExpressionParser
import type.Version

object DefaultParserFactory : ParserFactory {

    override fun createWithVersion(
        version: Version,
        nodeBuilder: NodeBuilder,
        tokenList: TokenStream,
    ): ParserInterface =
        when (version) {
            Version.VERSION_1_0 -> VOnePointZeroParserFactory().createParser(tokenList, nodeBuilder)
            Version.VERSION_1_1 -> VOnePointOneParserFactory().createParser(tokenList, nodeBuilder)
        }

    override fun createDefault(
        nodeBuilder: NodeBuilder,
        tokenList: TokenStream,
    ): ParserInterface = createWithVersion(Version.VERSION_1_1, nodeBuilder, tokenList)

    override fun createCustomParser(
        nodeBuilder: NodeBuilder,
        tokenList: TokenStream,
        expressionParser: ExpressionParser,
        statementParser: StatementParser,
    ): ParserInterface = Parser(tokenList, nodeBuilder, expressionParser, statementParser)
}
