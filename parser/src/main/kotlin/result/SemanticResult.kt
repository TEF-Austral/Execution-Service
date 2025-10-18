package parser.result

import Token
import node.Expression

sealed interface SemanticResult : ParserResult {
    fun identifier(): Token

    fun dataType(): Token

    fun initialValue(): Expression?
}
