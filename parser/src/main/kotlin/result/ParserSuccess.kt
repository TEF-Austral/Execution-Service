package parser.result

import parser.Parser

class ParserSuccess(
    private val message: String,
    private val parser: Parser,
) : ParserResult {
    override fun isSuccess(): Boolean = true

    override fun message(): String = "Successfully executed: $message"

    override fun getParser(): Parser = parser
    // override fun getPosition() = position
}
