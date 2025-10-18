package parser

import Token
import TokenStream
import builder.NodeBuilder
import node.EmptyExpression
import node.Statement
import parser.result.CompleteProgram
import parser.result.FailedProgram
import parser.result.FinalResult
import parser.result.NextResult
import parser.result.ParserError
import parser.result.ParserResult
import parser.result.ParserSuccess
import parser.statement.StatementParser
import parser.statement.expression.ExpressionParser
import parser.utils.checkType
import type.CommonTypes

data class Parser(
    private val stream: TokenStream,
    private val nodeBuilder: NodeBuilder,
    private val expressionParser: ExpressionParser,
    private val statementParser: StatementParser,
) : ParserInterface {

    override fun peak(): Token? = stream.peak()

    override fun getExpressionParser(): ExpressionParser = expressionParser

    override fun getNodeBuilder(): NodeBuilder = nodeBuilder

    override fun getStatementParser(): StatementParser = statementParser

    override fun hasNext(): Boolean = !isAtEnd()

    override fun isAtEnd(): Boolean = stream.isAtEnd()

    override fun parse(): FinalResult =
        try {
            val statements = parseStatements(this, emptyList())
            CompleteProgram(this, nodeBuilder.buildProgramNode(statements))
        } catch (e: Exception) {
            FailedProgram(this, e.message ?: "An unknown error occurred.")
        }

    private fun parseStatements(
        currentParser: Parser,
        accumulatedStatements: List<Statement>,
    ): List<Statement> {
        if (currentParser.peak() == null) {
            return accumulatedStatements
        }

        val result = currentParser.statementParser.parse(currentParser)
        return when {
            result.isSuccess() -> {
                val nextParser = result.getParser()
                val newStatement = result.getStatement()
                parseStatements(nextParser, accumulatedStatements + newStatement)
            }

            else -> {
                throw Exception(result.message())
            }
        }
    }

    fun advance(): Parser {
        if (isAtEnd()) {
            return this
        }
        val streamResult = stream.next() ?: return this

        return this.copy(stream = streamResult.nextStream)
    }

    fun consume(type: CommonTypes): ParserResult =
        if (tokenMatches(type)) {
            ParserSuccess("Token consumed: $type", advance())
        } else {
            val currentToken = peak()
            ParserError("Expected $type but found ${currentToken?.getType()}", this)
        }

    override fun next(): NextResult {
        try {
            if (!hasNext()) {
                return nextResultFailed("No more tokens available")
            }

            val result = statementParser.parse(this)

            return if (result.isSuccess()) {
                val nextParser = result.getParser()
                NextResult(result.getStatement(), true, "Parsed next node successfully", nextParser)
            } else {
                nextResultFailed("Failed to parse the next node: ${result.message()}")
            }
        } catch (e: Exception) {
            return nextResultFailed("Failed to parse the next node: ${e.message}")
        }
    }

    private fun tokenMatches(type: CommonTypes): Boolean =
        peak()?.let { checkType(type, it) } ?: false

    private fun nextResultFailed(message: String): NextResult =
        NextResult(EmptyExpression(), false, message, this)
}
