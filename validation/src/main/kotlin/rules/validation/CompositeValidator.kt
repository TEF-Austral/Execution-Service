package rules.validation

import node.ASTNode
import node.AssignmentStatement
import node.BinaryExpression
import node.DeclarationStatement
import node.ExpressionStatement
import node.IfStatement
import node.PrintStatement
import node.Program
import node.ReadInputExpression
import rules.AstValidator
import rules.ValidationRule

class CompositeValidator(
    private val rules: List<ValidationRule>,
) : AstValidator {

    override fun validate(root: ASTNode): List<String> = traverseAndValidate(root)

    private fun traverseAndValidate(node: ASTNode): List<String> {
        val errors = mutableListOf<String>()

        rules.forEach { rule ->
            errors.addAll(rule.validate(node))
        }

        when (node) {
            is Program -> {
                node.getStatements().forEach { stmt ->
                    errors.addAll(traverseAndValidate(stmt))
                }
            }

            is IfStatement -> {
                errors.addAll(traverseAndValidate(node.getCondition()))
                errors.addAll(traverseAndValidate(node.getConsequence()))
                node.getAlternative()?.let { elseStmt ->
                    errors.addAll(traverseAndValidate(elseStmt))
                }
            }

            is DeclarationStatement -> {
                node.getInitialValue()?.let { expr ->
                    errors.addAll(traverseAndValidate(expr))
                }
            }

            is AssignmentStatement -> {
                errors.addAll(traverseAndValidate(node.getValue()))
            }

            is PrintStatement -> {
                errors.addAll(traverseAndValidate(node.getExpression()))
            }

            is ExpressionStatement -> {
                errors.addAll(traverseAndValidate(node.getExpression()))
            }

            is BinaryExpression -> {
                errors.addAll(traverseAndValidate(node.getLeft()))
                errors.addAll(traverseAndValidate(node.getRight()))
            }

            is ReadInputExpression -> {
            }
        }

        return errors
    }
}
