package parser.factory

import TokenStream
import builder.NodeBuilder
import parser.Parser
import parser.statement.AssignmentParser
import parser.statement.ExpressionStatementBuilder
import parser.statement.IfStatement
import parser.statement.PrintParser
import parser.statement.expression.ReadEnvParser
import parser.statement.expression.ReadInputParser
import parser.statement.StatementParser
import parser.statement.VariableDeclarationParser
import parser.statement.binary.DefaultParseBinary
import parser.statement.expression.DelimitersBuilder
import parser.statement.expression.ExpressionParser
import parser.statement.expression.ExpressionRegistry
import parser.statement.expression.IdentifierBuilder
import parser.statement.expression.LiteralBuilder
import statement.enforcers.LetAssignmentEnforcer
import statement.enforcers.ColonEnforcer
import statement.enforcers.ConstAssignmentEnforcer
import statement.enforcers.ConstEnforcer
import statement.enforcers.DataTypeEnforcer
import statement.enforcers.IdentifierEnforcer
import statement.enforcers.LetEnforcer
import statement.enforcers.SemiColonEnforcer
import type.CommonTypes

class VOnePointOneParserFactory {
    fun createParser(
        tokens: TokenStream,
        nodeBuilder: NodeBuilder,
    ): Parser {
        val tokenToExpression =
            ExpressionRegistry(
                listOf(
                    IdentifierBuilder(),
                    LiteralBuilder(
                        listOf(
                            CommonTypes.NUMBER_LITERAL,
                            CommonTypes.STRING_LITERAL,
                            CommonTypes.BOOLEAN_LITERAL,
                        ),
                    ),
                    DelimitersBuilder(),
                    ReadInputParser(),
                    ReadEnvParser(),
                ),
            )
        val parseBinary =
            DefaultParseBinary(
                ExpressionRegistry(
                    listOf(
                        IdentifierBuilder(),
                        LiteralBuilder(
                            listOf(
                                CommonTypes.NUMBER_LITERAL,
                                CommonTypes.STRING_LITERAL,
                                CommonTypes.BOOLEAN_LITERAL,
                            ),
                        ),
                        DelimitersBuilder(),
                    ),
                ),
            )
        val expressionParser = ExpressionParser(tokenToExpression, parseBinary)
        val statementParser =
            StatementParser(
                listOf(
                    VariableDeclarationParser(
                        LetEnforcer(
                            IdentifierEnforcer(
                                ColonEnforcer(
                                    DataTypeEnforcer(
                                        LetAssignmentEnforcer(SemiColonEnforcer()),
                                        listOf(
                                            CommonTypes.NUMBER,
                                            CommonTypes.STRING,
                                            CommonTypes.BOOLEAN,
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                    VariableDeclarationParser(
                        ConstEnforcer(
                            IdentifierEnforcer(ConstAssignmentEnforcer(SemiColonEnforcer())),
                        ),
                    ),
                    VariableDeclarationParser(
                        ConstEnforcer(
                            IdentifierEnforcer(
                                ColonEnforcer(
                                    DataTypeEnforcer(
                                        ConstAssignmentEnforcer(SemiColonEnforcer()),
                                        listOf(
                                            CommonTypes.NUMBER,
                                            CommonTypes.STRING,
                                            CommonTypes.BOOLEAN,
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                    PrintParser(),
                    AssignmentParser(),
                    ExpressionStatementBuilder(
                        mapOf(
                            CommonTypes.NUMBER_LITERAL to true,
                            CommonTypes.STRING_LITERAL to true,
                            CommonTypes.BOOLEAN_LITERAL to true,
                            CommonTypes.IDENTIFIER to true,
                            CommonTypes.OPERATORS to true,
                            CommonTypes.READ_ENV to true,
                            CommonTypes.READ_INPUT to true,
                        ),
                    ),
                    IfStatement(),
                ),
            )
        return Parser(tokens, nodeBuilder, expressionParser, statementParser)
    }
}
