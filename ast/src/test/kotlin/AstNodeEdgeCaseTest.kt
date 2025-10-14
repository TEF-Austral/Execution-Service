import coordinates.Position
import builder.DefaultNodeBuilder
import node.EmptyStatement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import type.CommonTypes

class AstNodeEdgeCaseTest {
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
    fun `test nested IfStatement with multiple branches`() {
        val condition1 = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val condition2 = nodeBuilder.buildLiteralExpressionNode(numberToken)
        val thenBranch = nodeBuilder.buildPrintStatementNode(condition1)
        val elseBranch = nodeBuilder.buildIfStatementNode(condition2, thenBranch, null)

        val ifStatement = nodeBuilder.buildIfStatementNode(condition1, thenBranch, elseBranch)

        assertEquals(condition1, ifStatement.getCondition())
        assertEquals(thenBranch, ifStatement.getConsequence())
        assertEquals(elseBranch, ifStatement.getAlternative())
        assertTrue(ifStatement.hasAlternative())
    }

    @Test
    fun `test EmptyStatement with unexpected coordinates`() {
        val customCoordinates = Position(99, 99)
        val emptyStatement = EmptyStatement(customCoordinates)

        assertEquals(99, emptyStatement.getCoordinates().getRow())
        assertEquals(99, emptyStatement.getCoordinates().getColumn())
    }
}
