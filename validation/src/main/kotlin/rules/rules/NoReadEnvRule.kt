package rules.rules

import node.ASTNode
import node.ReadEnvExpression
import rules.ValidationRule

class NoReadEnvRule : ValidationRule {
    override fun validate(node: ASTNode): List<String> {
        if (node is ReadEnvExpression) {
            return listOf(
                "Security Error: Usage of 'readEnv' is prohibited. " +
                    "Position: ${node.getCoordinates()}",
            )
        }
        return emptyList()
    }
}
