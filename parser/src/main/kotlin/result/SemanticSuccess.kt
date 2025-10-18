package parser.result

import Token
import node.Expression
import parser.Parser

class SemanticSuccess(
    private val message: String,
    private val identifier: Token,
    private val dataType: Token,
    private val initialValue: Expression?,
    private val parser: Parser,
) : SemanticResult {
    override fun identifier(): Token = identifier

    override fun dataType(): Token = dataType

    override fun initialValue(): Expression? = initialValue

    override fun isSuccess(): Boolean = true

    override fun message(): String = message

    override fun getParser(): Parser = parser
}
