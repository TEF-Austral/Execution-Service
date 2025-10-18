package parser.factory

import TokenStream
import builder.NodeBuilder
import parser.statement.StatementParser
import parser.Parser
import parser.statement.AssignmentParser
import parser.statement.ExpressionStatementBuilder
import parser.statement.PrintParser
import parser.statement.VariableDeclarationParser
import parser.statement.expression.ExpressionParser
import parser.statement.binary.DefaultParseBinary
import parser.statement.expression.DelimitersBuilder
import parser.statement.expression.ExpressionRegistry
import parser.statement.expression.IdentifierBuilder
import parser.statement.expression.LiteralBuilder

class VOnePointZeroParserFactory {
    fun createParser(
        tokens: TokenStream,
        nodeBuilder: NodeBuilder,
    ): Parser {
        val tokenToExpression =
            ExpressionRegistry(
                listOf(
                    IdentifierBuilder(),
                    LiteralBuilder(),
                    DelimitersBuilder(),
                ),
            )
        val parseBinary =
            DefaultParseBinary(
                ExpressionRegistry(
                    listOf(
                        IdentifierBuilder(),
                        LiteralBuilder(),
                        DelimitersBuilder(),
                    ),
                ),
            )
        val expressionParser = ExpressionParser(tokenToExpression, parseBinary)
        val statementParser =
            StatementParser(
                listOf(
                    VariableDeclarationParser(),
                    PrintParser(),
                    AssignmentParser(),
                    ExpressionStatementBuilder(),
                ),
            )
        return Parser(tokens, nodeBuilder, expressionParser, statementParser)
    }
}
