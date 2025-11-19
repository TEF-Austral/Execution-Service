package rules

import node.ASTNode

interface AstValidator {
    fun validate(root: ASTNode): List<String>
}
