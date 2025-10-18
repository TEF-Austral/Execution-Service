package parser.result

import node.Program
import parser.Parser

class CompleteProgram(
    private val parser: Parser,
    private val program: Program,
) : FinalResult {
    override fun isSuccess(): Boolean = true

    override fun message(): String = "Parsed successfully"

    override fun getParser(): Parser = parser

    override fun getProgram(): Program = program
}
