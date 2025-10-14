package builder

import Token
import node.Program
import node.BinaryExpression
import node.IdentifierExpression
import node.LiteralExpression
import node.AssignmentStatement
import node.DeclarationStatement
import node.EmptyStatement
import node.Expression
import node.ExpressionStatement
import node.IfStatement
import node.PrintStatement
import node.ReadEnvExpression
import node.ReadInputExpression
import node.Statement

class DefaultNodeBuilder : NodeBuilder {
    override fun buildLiteralExpressionNode(token: Token): LiteralExpression =
        LiteralExpression(token, token.getCoordinates())

    override fun buildIdentifierNode(token: Token): IdentifierExpression =
        IdentifierExpression(token, token.getCoordinates())

    override fun buildBinaryExpressionNode(
        left: Expression,
        operator: Token,
        right: Expression,
        operatorWithSpacing: Token?,
    ): BinaryExpression =
        BinaryExpression(left, operator, right, operator.getCoordinates(), operatorWithSpacing)

    override fun buildVariableDeclarationStatementNode(
        declarationType: Token,
        identifier: Token,
        dataType: Token,
        initialValue: Expression?,
    ): DeclarationStatement =
        DeclarationStatement(
            declarationType,
            identifier,
            dataType,
            initialValue,
            identifier.getCoordinates(),
        )

    override fun buildAssignmentStatementNode(
        identifier: Token,
        value: Expression,
        assignmentOperator: Token?,
    ): AssignmentStatement =
        AssignmentStatement(identifier, value, value.getCoordinates(), assignmentOperator)

    override fun buildPrintStatementNode(expression: Expression): PrintStatement =
        PrintStatement(expression, expression.getCoordinates())

    override fun buildExpressionStatementNode(expression: Expression): ExpressionStatement =
        ExpressionStatement(expression, expression.getCoordinates())

    override fun buildEmptyStatementNode(): EmptyStatement = EmptyStatement()

    override fun buildProgramNode(statements: List<Statement>): Program = Program(statements)

    override fun buildIfStatementNode(
        condition: Expression,
        consequence: Statement,
        alternative: Statement?,
    ): IfStatement =
        IfStatement(
            condition,
            consequence,
            alternative,
            consequence.getCoordinates(),
        )

    override fun buildReadInputNode(printValue: Expression): ReadInputExpression =
        ReadInputExpression(printValue, printValue.getCoordinates())

    override fun buildReadEnvNode(envName: LiteralExpression): ReadEnvExpression =
        ReadEnvExpression(envName.getValue(), envName.getCoordinates())
}
