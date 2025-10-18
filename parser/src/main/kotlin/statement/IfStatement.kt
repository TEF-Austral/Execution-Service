package parser.statement

import Token
import node.Statement
import parser.Parser
import parser.result.StatementBuiltResult
import parser.result.StatementResult
import parser.utils.checkType
import parser.utils.isClosingBrace
import parser.utils.isOpeningParenthesis
import parser.utils.isClosingParenthesis
import parser.utils.isOpeningBrace
import type.CommonTypes

class IfStatement : StatementBuilder {
    override fun canHandle(
        token: Token?,
        parser: Parser,
    ): Boolean = checkType(CommonTypes.CONDITIONALS, token) && token?.getValue() == "if"

    override fun parse(parser: Parser): StatementResult {
        val ifKeyword = parser.consume(CommonTypes.CONDITIONALS)
        if (!ifKeyword.isSuccess()) {
            throw Exception("Expected 'if' keyword")
        }
        val afterIf = ifKeyword.getParser()
        if (!isOpeningParenthesis(afterIf)) {
            throw Exception("Expected '(' after 'if'")
        }
        val afterOpenParen = afterIf.advance()
        val conditionResult = afterOpenParen.getExpressionParser().parseExpression(afterOpenParen)
        if (!conditionResult.isSuccess()) {
            throw Exception("Expected condition expression: ${conditionResult.message()}")
        }
        if (!isClosingParenthesis(conditionResult.getParser())) {
            throw Exception("Expected ')' after condition")
        }
        val afterCloseParen = conditionResult.getParser().advance()

        val (afterThenStatement, thenStatement) = parseBlockWithBraces(afterCloseParen, "then")

        // --- LÓGICA 'ELSE' MEJORADA Y CORREGIDA ---
        val (finalParser, elseStatement) =
            if (afterThenStatement.hasNext() &&
                checkType(CommonTypes.CONDITIONALS, afterThenStatement.peak()) &&
                afterThenStatement.peak()?.getValue() == "else"
            ) {
                val afterElse = afterThenStatement.advance() // Consume 'else'

                // AHORA ES MÁS INTELIGENTE:
                // Después del 'else', ¿qué viene? ¿Una llave o algo más?
                if (isOpeningBrace(afterElse)) {
                    // Si es una llave, es un bloque 'else { ... }'
                    // Usamos la función que ya sabe analizar bloques.
                    parseBlockWithBraces(afterElse, "else")
                } else {
                    // Si NO es una llave, es otra cosa (como un 'else if').
                    // Dejamos que el despachador principal se encargue.
                    val elseResult = afterElse.getStatementParser().parse(afterElse)
                    if (!elseResult.isSuccess()) {
                        throw Exception("Error parsing else statement: ${elseResult.message()}")
                    }
                    Pair(elseResult.getParser(), elseResult.getStatement())
                }
            } else {
                Pair(afterThenStatement, null) // No hay 'else'
            }

        val ifStatement =
            finalParser.getNodeBuilder().buildIfStatementNode(
                conditionResult.getExpression(),
                thenStatement,
                elseStatement,
            )

        return StatementBuiltResult(finalParser, ifStatement)
    }

    // Versión original (limitada a un solo enunciado) que reutilizamos.
    private fun parseBlockWithBraces(
        parser: Parser,
        blockType: String,
    ): Pair<Parser, Statement> {
        if (!isOpeningBrace(parser)) {
            throw Exception(
                "Expected '{' to start $blockType block but found ${parser.peak()?.getValue()}",
            )
        }
        val afterOpenBrace = parser.advance()
        val statementResult = afterOpenBrace.getStatementParser().parse(afterOpenBrace)
        if (!statementResult.isSuccess()) {
            throw Exception("Error parsing $blockType statement: ${statementResult.message()}")
        }
        val afterStatement = statementResult.getParser()
        if (!isClosingBrace(afterStatement)) {
            throw Exception("Expected '}' to close $blockType block")
        }
        val afterCloseBrace = afterStatement.advance()
        return Pair(afterCloseBrace, statementResult.getStatement())
    }
}
