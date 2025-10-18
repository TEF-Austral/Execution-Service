package parser.statement

import PrintScriptToken
import Token
import statement.enforcers.LetAssignmentEnforcer
import statement.enforcers.ColonEnforcer
import statement.enforcers.DataTypeEnforcer
import statement.enforcers.LetEnforcer
import statement.enforcers.IdentifierEnforcer
import statement.enforcers.SemanticEnforcers
import statement.enforcers.SemiColonEnforcer
import coordinates.Position
import parser.Parser
import parser.result.SemanticSuccess
import parser.result.StatementBuiltResult
import parser.result.StatementErrorResult
import parser.result.StatementResult
import type.CommonTypes

class VariableDeclarationParser(
    val semanticOrder: SemanticEnforcers =
        LetEnforcer(
            IdentifierEnforcer(
                ColonEnforcer(
                    DataTypeEnforcer(
                        LetAssignmentEnforcer(
                            SemiColonEnforcer(),
                        ),
                    ),
                ),
            ),
        ),
) : StatementBuilder {
    override fun canHandle(
        token: Token?,
        parser: Parser,
    ): Boolean = token?.getType() == CommonTypes.LET || token?.getType() == CommonTypes.CONST

    override fun parse(parser: Parser): StatementResult {
        val emptyToken = PrintScriptToken(CommonTypes.EMPTY, "", Position(0, 0))
        val emptySemanticResult =
            SemanticSuccess("", emptyToken, emptyToken, null, parser)
        val result = semanticOrder.enforce(emptySemanticResult)
        if (!result.isSuccess()) {
            return StatementErrorResult(result.getParser(), result.message())
        }
        val builtStatement =
            result.getParser().getNodeBuilder().buildVariableDeclarationStatementNode(
                parser.peak()!!,
                result.identifier(),
                result.dataType(),
                result.initialValue(),
            )
        return StatementBuiltResult(result.getParser(), builtStatement)
    }
}
