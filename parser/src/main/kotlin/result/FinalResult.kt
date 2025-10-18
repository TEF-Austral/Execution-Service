package parser.result

import node.Program

interface FinalResult : ParserResult {
    fun getProgram(): Program
}
