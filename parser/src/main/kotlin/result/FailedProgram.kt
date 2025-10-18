package parser.result

import node.EmptyStatement
import node.Program
import parser.Parser

class FailedProgram(
    private val parser: Parser,
    private val message: String,
) : FinalResult {
    override fun getProgram(): Program = Program(listOf(EmptyStatement()))

    override fun isSuccess(): Boolean = false

    override fun message(): String = message

    override fun getParser(): Parser = parser
}
