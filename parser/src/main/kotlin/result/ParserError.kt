package parser.result

import parser.Parser

class ParserError(
    private val message: String,
    private val parser: Parser,
) : ParserResult {
    override fun isSuccess(): Boolean = false

    override fun message(): String = "Error occurred: $message"

    override fun getParser(): Parser = parser
    // override fun getPosition(): Position = position
}
