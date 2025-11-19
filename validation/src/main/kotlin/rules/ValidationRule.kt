package rules

import node.ASTNode

interface ValidationRule {
    fun validate(node: ASTNode): List<String>
}
