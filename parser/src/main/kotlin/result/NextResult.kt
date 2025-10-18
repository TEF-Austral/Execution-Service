package parser.result

import node.ASTNode
import parser.ParserInterface

data class NextResult(
    val node: ASTNode,
    val isSuccess: Boolean,
    val message: String,
    val parser: ParserInterface,
)
