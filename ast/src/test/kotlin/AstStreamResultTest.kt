import coordinates.Position
import node.ASTNode
import node.EmptyExpression
import node.LiteralExpression
import node.IdentifierExpression
import node.BinaryExpression
import node.PrintStatement
import node.AssignmentStatement
import node.DeclarationStatement
import node.Program
import builder.DefaultNodeBuilder
import coordinates.UnassignedPosition
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import type.CommonTypes
import java.util.NoSuchElementException
import stream.AstStream
import stream.AstStreamResult

class AstStreamResultTest {
    private lateinit var nodeBuilder: DefaultNodeBuilder
    private lateinit var mockToken: Token
    private lateinit var numberToken: Token
    private lateinit var identifierToken: Token
    private lateinit var operatorToken: Token
    private lateinit var dataTypeToken: Token
    private lateinit var declarationTypeToken: Token

    @BeforeEach
    fun setUp() {
        nodeBuilder = DefaultNodeBuilder()

        val coordinates = Position(1, 1)
        mockToken = PrintScriptToken(CommonTypes.STRING_LITERAL, "test", coordinates)
        numberToken = PrintScriptToken(CommonTypes.NUMBER_LITERAL, "42", coordinates)
        identifierToken = PrintScriptToken(CommonTypes.IDENTIFIER, "myVar", coordinates)
        operatorToken = PrintScriptToken(CommonTypes.OPERATORS, "+", coordinates)
        dataTypeToken = PrintScriptToken(CommonTypes.NUMBER, "NUMBER", coordinates)
        declarationTypeToken = PrintScriptToken(CommonTypes.LET, "let", coordinates)
    }

    @Test
    fun testAstStreamResultCreation() {
        val node = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val nextStream = MockAstStream(listOf())

        val result = AstStreamResult(node, nextStream, true)

        assertNotNull(result)
        assertEquals(node, result.node)
        assertEquals(nextStream, result.nextStream)
        assertEquals(Position(1, 1), result.node.getCoordinates())
    }

    @Test
    fun testAstStreamResultWithLiteralExpression() {
        val literalExpr = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val nextStream = MockAstStream(listOf())

        val result = AstStreamResult(literalExpr, nextStream, true)

        assertEquals(literalExpr, result.node)
        assertEquals("test", (result.node as LiteralExpression).getValue())
        assertEquals(CommonTypes.STRING_LITERAL, result.node.getType())
        assertSame(nextStream, result.nextStream)
        assertEquals(Position(1, 1), result.node.getCoordinates())
    }

    @Test
    fun testAstStreamResultWithIdentifierExpression() {
        val identifierExpr = nodeBuilder.buildIdentifierNode(identifierToken)
        val nextStream = MockAstStream(listOf())

        val result = AstStreamResult(identifierExpr, nextStream, true)

        assertEquals(identifierExpr, result.node)
        assertEquals("myVar", (result.node as IdentifierExpression).getValue())
        assertEquals(identifierToken, result.node.getToken())
        assertSame(nextStream, result.nextStream)
        assertEquals(Position(1, 1), result.node.getCoordinates())
    }

    @Test
    fun testAstStreamResultWithBinaryExpression() {
        val leftExpr = nodeBuilder.buildLiteralExpressionNode(numberToken)
        val rightExpr = nodeBuilder.buildIdentifierNode(identifierToken)
        val binaryExpr = nodeBuilder.buildBinaryExpressionNode(leftExpr, operatorToken, rightExpr)
        val nextStream = MockAstStream(listOf())

        val result = AstStreamResult(binaryExpr, nextStream, true)

        assertEquals(binaryExpr, result.node)
        val binary = result.node as BinaryExpression
        assertEquals(leftExpr, binary.getLeft())
        assertEquals("+", binary.getOperator())
        assertEquals(rightExpr, binary.getRight())
        assertSame(nextStream, result.nextStream)
        assertEquals(Position(1, 1), result.node.getCoordinates())
    }

    @Test
    fun testAstStreamResultWithEmptyExpression() {
        val emptyExpr = EmptyExpression()
        val nextStream = MockAstStream(listOf())

        val result = AstStreamResult(emptyExpr, nextStream, true)

        assertEquals(emptyExpr, result.node)
        assertEquals("EmptyExpression", result.node.toString())
        assertSame(nextStream, result.nextStream)
        assertTrue(result.node.getCoordinates() is UnassignedPosition)
    }

    @Test
    fun testAstStreamResultWithStatements() {
        val emptyStmt = nodeBuilder.buildEmptyStatementNode()
        val nextStream = MockAstStream(listOf())

        val result = AstStreamResult(emptyStmt, nextStream, true)

        assertEquals(emptyStmt, result.node)
        assertEquals("EmptyStatement", result.node.toString())
        assertSame(nextStream, result.nextStream)
        assertTrue(result.node.getCoordinates() is UnassignedPosition)
    }

    @Test
    fun testAstStreamResultWithPrintStatement() {
        val expression = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val printStmt = nodeBuilder.buildPrintStatementNode(expression)
        val nextStream = MockAstStream(listOf())

        val result = AstStreamResult(printStmt, nextStream, true)

        assertEquals(printStmt, result.node)
        val print = result.node as PrintStatement
        assertEquals(expression, print.getExpression())
        assertSame(nextStream, result.nextStream)
        assertEquals(Position(1, 1), result.node.getCoordinates())
    }

    @Test
    fun testAstStreamResultWithAssignmentStatement() {
        val value = nodeBuilder.buildLiteralExpressionNode(numberToken)
        val assignment = nodeBuilder.buildAssignmentStatementNode(identifierToken, value)
        val nextStream = MockAstStream(listOf())

        val result = AstStreamResult(assignment, nextStream, true)

        assertEquals(assignment, result.node)
        val assign = result.node as AssignmentStatement
        assertEquals("myVar", assign.getIdentifier())
        assertEquals(value, assign.getValue())
        assertSame(nextStream, result.nextStream)
        assertEquals(Position(1, 1), result.node.getCoordinates())
    }

    @Test
    fun testAstStreamResultWithDeclarationStatement() {
        val initialValue = nodeBuilder.buildLiteralExpressionNode(numberToken)
        val varDecl =
            nodeBuilder.buildVariableDeclarationStatementNode(
                declarationTypeToken,
                identifierToken,
                dataTypeToken,
                initialValue,
            )
        val nextStream = MockAstStream(listOf())

        val result = AstStreamResult(varDecl, nextStream, true)

        assertEquals(varDecl, result.node)
        val decl = result.node as DeclarationStatement
        assertEquals("myVar", decl.getIdentifier())
        assertEquals(CommonTypes.NUMBER, decl.getDataType())
        assertEquals(initialValue, decl.getInitialValue())
        assertSame(nextStream, result.nextStream)
        assertEquals(Position(1, 1), result.node.getCoordinates())
    }

    @Test
    fun testAstStreamResultWithProgram() {
        val stmt1 = nodeBuilder.buildEmptyStatementNode()
        val stmt2 =
            nodeBuilder.buildPrintStatementNode(
                nodeBuilder.buildLiteralExpressionNode(mockToken),
            )
        val statements = listOf(stmt1, stmt2)
        val program = nodeBuilder.buildProgramNode(statements)
        val nextStream = MockAstStream(listOf())

        val result = AstStreamResult(program, nextStream, true)

        assertEquals(program, result.node)
        val prog = result.node as Program
        assertEquals(2, prog.getStatements().size)
        assertEquals(statements, prog.getStatements())
        assertSame(nextStream, result.nextStream)
        assertTrue(result.node.getCoordinates() is UnassignedPosition)
    }

    @Test
    fun testAstStreamResultDataClassProperties() {
        val node = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val nextStream = MockAstStream(listOf())

        val result1 = AstStreamResult(node, nextStream, true)
        val result2 = AstStreamResult(node, nextStream, true)

        // Test equality (data class should provide equals)
        assertEquals(result1, result2)
        assertEquals(result1.hashCode(), result2.hashCode())

        // Test toString (data class should provide meaningful toString)
        val toString = result1.toString()
        assertTrue(toString.contains("AstStreamResult"))
    }

    @Test
    fun testAstStreamResultWithDifferentNodes() {
        val node1 = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val node2 = nodeBuilder.buildIdentifierNode(identifierToken)
        val nextStream = MockAstStream(listOf())

        val result1 = AstStreamResult(node1, nextStream, true)
        val result2 = AstStreamResult(node2, nextStream, true)

        assertEquals(node1, result1.node)
        assertEquals(node2, result2.node)
        assertEquals(nextStream, result1.nextStream)
        assertEquals(nextStream, result2.nextStream)
    }

    @Test
    fun testAstStreamResultWithDifferentStreams() {
        val node = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val stream1 = MockAstStream(listOf())
        val stream2 = MockAstStream(listOf(nodeBuilder.buildEmptyStatementNode()))

        val result1 = AstStreamResult(node, stream1, true)
        val result2 = AstStreamResult(node, stream2, true)

        assertEquals(node, result1.node)
        assertEquals(node, result2.node)
        assertEquals(stream1, result1.nextStream)
        assertEquals(stream2, result2.nextStream)
    }

    @Test
    fun testAstStreamResultCoordinatesPreservation() {
        val customCoords = Position(5, 10)
        val tokenWithCoords = PrintScriptToken(CommonTypes.NUMBER_LITERAL, "123", customCoords)
        val node = nodeBuilder.buildLiteralExpressionNode(tokenWithCoords)
        val nextStream = MockAstStream(listOf())

        val result = AstStreamResult(node, nextStream, true)

        assertEquals(customCoords, result.node.getCoordinates())
        assertEquals(5, result.node.getCoordinates().getRow())
        assertEquals(10, result.node.getCoordinates().getColumn())
    }

    @Test
    fun testAstStreamResultWithComplexNodes() {
        val leftOperand =
            nodeBuilder.buildLiteralExpressionNode(
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 15)),
            )
        val rightOperand =
            nodeBuilder.buildLiteralExpressionNode(
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "10", Position(1, 19)),
            )
        val binaryExpr =
            nodeBuilder.buildBinaryExpressionNode(
                leftOperand,
                operatorToken,
                rightOperand,
            )

        val varDecl =
            nodeBuilder.buildVariableDeclarationStatementNode(
                declarationTypeToken,
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.NUMBER, "NUMBER", Position(1, 8)),
                binaryExpr,
            )

        val nextStream = MockAstStream(listOf())
        val result = AstStreamResult(varDecl, nextStream, true)

        val statement = result.node as DeclarationStatement
        assertEquals("x", statement.getIdentifier())
        assertEquals(CommonTypes.NUMBER, statement.getDataType())
        assertNotNull(statement.getInitialValue())

        val initialValueBinary = statement.getInitialValue() as BinaryExpression
        assertEquals("5", (initialValueBinary.getLeft() as LiteralExpression).getValue())
        assertEquals("+", initialValueBinary.getOperator())
        assertEquals("10", (initialValueBinary.getRight() as LiteralExpression).getValue())
        assertSame(nextStream, result.nextStream)
    }

    @Test
    fun testAstStreamResultWithStreamThatHasNodes() {
        val node = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val nextNode = nodeBuilder.buildIdentifierNode(identifierToken)
        val streamWithNodes = MockAstStream(listOf(nextNode))

        val result = AstStreamResult(node, streamWithNodes, true)

        assertEquals(node, result.node)
        assertEquals(streamWithNodes, result.nextStream)
        assertFalse(result.nextStream.isAtEnd())
        assertEquals(nextNode, result.nextStream.peak())
    }

    @Test
    fun testAstStreamResultWithEmptyStream() {
        val node = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val emptyStream = MockAstStream(listOf())

        val result = AstStreamResult(node, emptyStream, false)

        assertEquals(node, result.node)
        assertEquals(emptyStream, result.nextStream)
        assertTrue(result.nextStream.isAtEnd())
    }

    private class MockAstStream(
        private val nodes: List<ASTNode>,
    ) : AstStream {
        private var index = 0

        override fun peak(): ASTNode? =
            if (index < nodes.size) {
                nodes[index]
            } else {
                null
            }

        override fun next(): AstStreamResult {
            if (isAtEnd()) {
                throw NoSuchElementException("No more elements in stream")
            }

            val currentNode = nodes[index]
            index++

            val remainingNodes = nodes.drop(index)
            val nextStream = MockAstStream(remainingNodes)

            return AstStreamResult(currentNode, nextStream, true)
        }

        override fun isAtEnd(): Boolean = index >= nodes.size
    }
}
