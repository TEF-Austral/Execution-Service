package parser.result

import parser.Parser

sealed interface ParserResult {
    fun isSuccess(): Boolean

    fun message(): String

    fun getParser(): Parser
    // fun getPosition(): Position
}
