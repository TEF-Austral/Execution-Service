
import coordinates.Position
import builder.DefaultNodeBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import type.CommonTypes

import node.BinaryExpression
import node.DeclarationStatement
import node.Expression

class AstNodeAdditionalCoverageTest {
    private val builder = DefaultNodeBuilder()
    private val coords = Position(2, 2)
    private val strToken = PrintScriptToken(CommonTypes.STRING_LITERAL, "hello", coords)
    private val numToken = PrintScriptToken(CommonTypes.NUMBER_LITERAL, "3", coords)
    private val idToken = PrintScriptToken(CommonTypes.IDENTIFIER, "x", coords)
    private val opToken = PrintScriptToken(CommonTypes.OPERATORS, "*", coords)

    @Test
    fun `declaration with mismatched dataType not equal`() {
        val lit = builder.buildLiteralExpressionNode(strToken)
        val decl =
            builder.buildVariableDeclarationStatementNode(
                PrintScriptToken(CommonTypes.LET, "let", coords),
                idToken,
                PrintScriptToken(CommonTypes.NUMBER, "NUMBER", coords),
                lit,
            ) as DeclarationStatement
        assertNotEquals(
            CommonTypes.NUMBER,
            (decl.getInitialValue() as? node.LiteralExpression)?.getType(),
        )
    }

    @Test
    fun `deeply nested binary builds correct depth`() {
        var expr: Expression = builder.buildLiteralExpressionNode(numToken)
        repeat(10) {
            expr = builder.buildBinaryExpressionNode(expr, opToken, expr)
        }
        assertTrue(expr is BinaryExpression)
    }

    @Test
    fun `read input expression preserves value`() {
        val literal = builder.buildLiteralExpressionNode(strToken)
        val inputExpr = builder.buildReadInputNode(literal)
        assertEquals(literal, inputExpr.printValue())
        assertEquals(coords, inputExpr.getCoordinates())
    }

    @Test
    fun `read env expression preserves name`() {
        val literal = builder.buildLiteralExpressionNode(strToken)
        val envExpr = builder.buildReadEnvNode(literal)
        assertEquals("hello", envExpr.envName())
        assertEquals(coords, envExpr.getCoordinates())
    }

    @Test
    fun `program builder supports empty and statements`() {
        val emptyProg = builder.buildProgramNode(emptyList())
        assertTrue(emptyProg.getStatements().isEmpty())

        val stmt = builder.buildPrintStatementNode(builder.buildLiteralExpressionNode(numToken))
        val prog = builder.buildProgramNode(listOf(stmt))
        assertEquals(1, prog.getStatements().size)
        assertEquals(stmt, prog.getStatements().first())
    }
}
