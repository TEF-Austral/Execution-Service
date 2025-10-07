package node

import coordinates.Position
import PrintScriptToken
import Token
import builder.DefaultNodeBuilder
import coordinates.UnassignedPosition
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import type.CommonTypes
import kotlin.toString

class NodeTest {
    private lateinit var nodeBuilder: DefaultNodeBuilder
    private lateinit var mockToken: Token
    private lateinit var numberToken: Token
    private lateinit var identifierToken: Token
    private lateinit var operatorToken: Token
    private lateinit var dataTypeToken: Token
    private lateinit var declarationTypeToken: Token
    private lateinit var booleanToken: Token
    private lateinit var stringToken: Token
    private lateinit var constToken: Token

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
        booleanToken = PrintScriptToken(CommonTypes.BOOLEAN_LITERAL, "true", coordinates)
        stringToken = PrintScriptToken(CommonTypes.STRING, "STRING", coordinates)
        constToken = PrintScriptToken(CommonTypes.CONST, "const", coordinates)
    }

    @Test
    fun testLiteralExpression() {
        val literalExpr = nodeBuilder.buildLiteralExpressionNode(mockToken)

        assertEquals("test", literalExpr.getValue())
        assertNotNull(literalExpr)
    }

    @Test
    fun testIdentifierExpression() {
        val identifierExpr = nodeBuilder.buildIdentifierNode(identifierToken)

        assertEquals("myVar", identifierExpr.getValue())
        assertNotNull(identifierExpr)
    }

    @Test
    fun testBinaryExpression() {
        val leftExpr = nodeBuilder.buildLiteralExpressionNode(numberToken)
        val rightExpr = nodeBuilder.buildIdentifierNode(identifierToken)

        val binaryExpr = nodeBuilder.buildBinaryExpressionNode(leftExpr, operatorToken, rightExpr)

        assertEquals(leftExpr, binaryExpr.getLeft())
        assertEquals("+", binaryExpr.getOperator())
        assertEquals(rightExpr, binaryExpr.getRight())
    }

    @Test
    fun testVariableDeclarationStatement() {
        val initialValue = nodeBuilder.buildLiteralExpressionNode(numberToken)

        val varDecl =
            nodeBuilder.buildVariableDeclarationStatementNode(
                declarationTypeToken,
                identifierToken,
                dataTypeToken,
                initialValue,
            )

        assertEquals("myVar", varDecl.getIdentifier())
        assertEquals(CommonTypes.NUMBER, varDecl.getDataType())
        assertEquals(initialValue, varDecl.getInitialValue())
    }

    @Test
    fun testVariableDeclarationStatementWithoutInitialValue() {
        val varDecl =
            nodeBuilder.buildVariableDeclarationStatementNode(
                declarationTypeToken,
                identifierToken,
                dataTypeToken,
            )

        assertEquals("myVar", varDecl.getIdentifier())
        assertEquals(CommonTypes.NUMBER, varDecl.getDataType())
        assertNull(varDecl.getInitialValue())
    }

    @Test
    fun testAssignmentStatement() {
        val value = nodeBuilder.buildLiteralExpressionNode(numberToken)

        val assignment = nodeBuilder.buildAssignmentStatementNode(identifierToken, value)

        assertEquals("myVar", assignment.getIdentifier())
        assertEquals(value, assignment.getValue())
    }

    @Test
    fun testPrintStatement() {
        val expression = nodeBuilder.buildLiteralExpressionNode(mockToken)

        val printStmt = nodeBuilder.buildPrintStatementNode(expression)

        assertEquals(expression, printStmt.getExpression())
    }

    @Test
    fun testExpressionStatement() {
        val expression = nodeBuilder.buildIdentifierNode(identifierToken)

        val exprStmt = nodeBuilder.buildExpressionStatementNode(expression)

        assertEquals(expression, exprStmt.getExpression())
    }

    @Test
    fun testEmptyStatement() {
        val emptyStmt = nodeBuilder.buildEmptyStatementNode()

        assertNotNull(emptyStmt)
        assertEquals("EmptyStatement", emptyStmt.toString())
    }

    @Test
    fun testProgram() {
        val stmt1 = nodeBuilder.buildEmptyStatementNode()
        val stmt2 =
            nodeBuilder.buildPrintStatementNode(
                nodeBuilder.buildLiteralExpressionNode(mockToken),
            )
        val statements = listOf(stmt1, stmt2)

        val program = nodeBuilder.buildProgramNode(statements)

        assertEquals(2, program.getStatements().size)
        assertEquals(statements, program.getStatements())
    }

    @Test
    fun testEmptyExpression() {
        val emptyExpr = EmptyExpression()

        assertEquals("EmptyExpression", emptyExpr.toString())
    }

    @Test
    fun testComplexAST() {
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

        val program = nodeBuilder.buildProgramNode(listOf(varDecl))

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0] as DeclarationStatement
        assertEquals("x", statement.getIdentifier())
        assertEquals(CommonTypes.NUMBER, statement.getDataType())
        assertNotNull(statement.getInitialValue())

        val initialValueBinary = statement.getInitialValue() as BinaryExpression
        assertEquals("5", (initialValueBinary.getLeft() as LiteralExpression).getValue())
        assertEquals("+", initialValueBinary.getOperator())
        assertEquals("10", (initialValueBinary.getRight() as LiteralExpression).getValue())
    }

    @Test
    fun `Simple If Statement with Nodebuilder`() {
        val condition = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val thenBranch = nodeBuilder.buildEmptyStatementNode()
        val elseBranch = nodeBuilder.buildEmptyStatementNode()
        val ifStatement = nodeBuilder.buildIfStatementNode(condition, thenBranch, elseBranch)
        assertEquals(condition, ifStatement.getCondition())
        assertEquals(thenBranch, ifStatement.getConsequence())
        assertEquals(elseBranch, ifStatement.getAlternative())
        assertEquals(true, ifStatement.hasAlternative())
    }

    @Test
    fun `Simple If Statement`() {
        val condition = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val thenBranch = nodeBuilder.buildEmptyStatementNode()
        val elseBranch = nodeBuilder.buildEmptyStatementNode()
        val ifStatement = IfStatement(condition, thenBranch, elseBranch, Position(1, 1))
        assertEquals(condition, ifStatement.getCondition())
        assertEquals(thenBranch, ifStatement.getConsequence())
        assertEquals(elseBranch, ifStatement.getAlternative())
        assertEquals(true, ifStatement.hasAlternative())
    }

    @Test
    fun `Simple If Statement Without Else`() {
        val condition = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val thenBranch = nodeBuilder.buildEmptyStatementNode()
        val ifStatement = IfStatement(condition, thenBranch, null, Position(1, 1))
        assertEquals(condition, ifStatement.getCondition())
        assertEquals(thenBranch, ifStatement.getConsequence())
        assertEquals(null, ifStatement.getAlternative())
        assertEquals(false, ifStatement.hasAlternative())
    }

    @Test
    fun testAssignmentStatementGetIdentifierToken() {
        val value = nodeBuilder.buildLiteralExpressionNode(numberToken)
        val assignment = nodeBuilder.buildAssignmentStatementNode(identifierToken, value)

        assertEquals(identifierToken, assignment.getIdentifierToken())
    }

    @Test
    fun testPrintStatementGetExpressionTokenWithIdentifier() {
        val identifierExpr = nodeBuilder.buildIdentifierNode(identifierToken)
        val printStmt = nodeBuilder.buildPrintStatementNode(identifierExpr)

        assertEquals(identifierToken, printStmt.getExpressionToken())
    }

    @Test
    fun testPrintStatementGetExpressionTokenWithNonIdentifier() {
        val literalExpr = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val printStmt = nodeBuilder.buildPrintStatementNode(literalExpr)

        assertNull(printStmt.getExpressionToken())
    }

    @Test
    fun testIfStatementGetAlternativeWhenNull() {
        val condition = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val thenBranch = nodeBuilder.buildEmptyStatementNode()
        val ifStatement = nodeBuilder.buildIfStatementNode(condition, thenBranch, null)

        assertNull(ifStatement.getAlternative())
    }

    @Test
    fun testEmptyExpressionToString() {
        val emptyExpr = EmptyExpression()

        assertEquals("EmptyExpression", emptyExpr.toString())
    }

    @Test
    fun testLiteralExpressionGetType() {
        val literalExpr = nodeBuilder.buildLiteralExpressionNode(numberToken)

        assertEquals(CommonTypes.NUMBER_LITERAL, literalExpr.getType())
    }

    @Test
    fun testProgramWithNoStatements() {
        val program = nodeBuilder.buildProgramNode(emptyList())

        assertEquals(0, program.getStatements().size)
    }

    @Test
    fun testEmptyExpressionWithDefaultCoordinates() {
        val emptyExpr = EmptyExpression()
        assertEquals("EmptyExpression", emptyExpr.toString())
        assertEquals(-1, emptyExpr.getCoordinates().getRow())
        assertEquals(-1, emptyExpr.getCoordinates().getColumn())
    }

    @Test
    fun testEmptyExpressionWithCustomCoordinates() {
        val customCoordinates = Position(5, 10)
        val emptyExpr = EmptyExpression(customCoordinates)
        assertEquals("EmptyExpression", emptyExpr.toString())
        assertEquals(5, emptyExpr.getCoordinates().getRow())
        assertEquals(10, emptyExpr.getCoordinates().getColumn())
    }

    @Test
    fun testBinaryExpressionAllMethods() {
        val leftExpr = nodeBuilder.buildLiteralExpressionNode(numberToken)
        val rightExpr = nodeBuilder.buildIdentifierNode(identifierToken)
        val minusOperator = PrintScriptToken(CommonTypes.OPERATORS, "-", Position(2, 5))

        val binaryExpr = nodeBuilder.buildBinaryExpressionNode(leftExpr, minusOperator, rightExpr)

        assertEquals(leftExpr, binaryExpr.getLeft())
        assertEquals("-", binaryExpr.getOperator())
        assertEquals(rightExpr, binaryExpr.getRight())
        assertEquals(2, binaryExpr.getCoordinates().getRow())
        assertEquals(5, binaryExpr.getCoordinates().getColumn())
    }

    @Test
    fun testBinaryExpressionWithDifferentOperators() {
        val leftExpr = nodeBuilder.buildLiteralExpressionNode(numberToken)
        val rightExpr = nodeBuilder.buildLiteralExpressionNode(numberToken)

        val operators = listOf("*", "/", "%", "==", "!=", "<", ">", "<=", ">=", "&&", "||")

        operators.forEach { op ->
            val operator = PrintScriptToken(CommonTypes.OPERATORS, op, Position(1, 1))
            val binaryExpr = nodeBuilder.buildBinaryExpressionNode(leftExpr, operator, rightExpr)
            assertEquals(op, binaryExpr.getOperator())
        }
    }

    @Test
    fun testPrintStatementWithIdentifierExpression() {
        val identifierExpr = nodeBuilder.buildIdentifierNode(identifierToken)
        val printStmt = nodeBuilder.buildPrintStatementNode(identifierExpr)

        assertEquals(identifierExpr, printStmt.getExpression())
        assertEquals(identifierToken, printStmt.getExpressionToken())
        assertNotNull(printStmt.getExpressionToken())
    }

    @Test
    fun testPrintStatementWithLiteralExpression() {
        val literalExpr = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val printStmt = nodeBuilder.buildPrintStatementNode(literalExpr)

        assertEquals(literalExpr, printStmt.getExpression())
        assertNull(printStmt.getExpressionToken())
    }

    @Test
    fun testReadInputStatement() {
        val literalExpr = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val readInput = nodeBuilder.buildReadInputNode(literalExpr)

        assertEquals(literalExpr, readInput.printValue())
    }

    @Test
    fun testReadEnvStatement() {
        val literalExpr = nodeBuilder.buildLiteralExpressionNode(mockToken)
        val readEnv = nodeBuilder.buildReadEnvNode(literalExpr)

        assertEquals(literalExpr.getValue(), readEnv.envName())
    }

    @Test
    fun testPrintStatementWithBinaryExpression() {
        val leftExpr = nodeBuilder.buildLiteralExpressionNode(numberToken)
        val rightExpr = nodeBuilder.buildLiteralExpressionNode(numberToken)
        val binaryExpr = nodeBuilder.buildBinaryExpressionNode(leftExpr, operatorToken, rightExpr)
        val printStmt = nodeBuilder.buildPrintStatementNode(binaryExpr)

        assertEquals(binaryExpr, printStmt.getExpression())
        assertNull(printStmt.getExpressionToken())
    }

    @Test
    fun testProgramWithMultipleStatementTypes() {
        val emptyStmt = nodeBuilder.buildEmptyStatementNode()
        val printStmt =
            nodeBuilder.buildPrintStatementNode(
                nodeBuilder.buildLiteralExpressionNode(mockToken),
            )
        val assignmentStmt =
            nodeBuilder.buildAssignmentStatementNode(
                identifierToken,
                nodeBuilder.buildLiteralExpressionNode(numberToken),
            )
        val declStmt =
            nodeBuilder.buildVariableDeclarationStatementNode(
                declarationTypeToken,
                identifierToken,
                dataTypeToken,
                nodeBuilder.buildLiteralExpressionNode(numberToken),
            )

        val statements = listOf(emptyStmt, printStmt, assignmentStmt, declStmt)
        val program = nodeBuilder.buildProgramNode(statements)

        assertEquals(4, program.getStatements().size)
        assertEquals(statements, program.getStatements())
        assertEquals(-1, program.getCoordinates().getRow())
        assertEquals(-1, program.getCoordinates().getColumn())
    }

    @Test
    fun testEmptyStatementWithDefaultCoordinates() {
        val emptyStmt = nodeBuilder.buildEmptyStatementNode()
        assertEquals("EmptyStatement", emptyStmt.toString())
        assertEquals(-1, emptyStmt.getCoordinates().getRow())
        assertEquals(-1, emptyStmt.getCoordinates().getColumn())
    }

    @Test
    fun testEmptyStatementWithCustomCoordinates() {
        val customCoordinates = Position(3, 7)
        val emptyStmt = EmptyStatement(customCoordinates)
        assertEquals("EmptyStatement", emptyStmt.toString())
        assertEquals(3, emptyStmt.getCoordinates().getRow())
        assertEquals(7, emptyStmt.getCoordinates().getColumn())
    }

    @Test
    fun testLiteralExpressionWithDifferentTypes() {
        val stringLiteral = PrintScriptToken(CommonTypes.STRING_LITERAL, "hello", Position(1, 1))
        val numberLiteral = PrintScriptToken(CommonTypes.NUMBER_LITERAL, "3.14", Position(2, 2))
        val booleanLiteral = PrintScriptToken(CommonTypes.BOOLEAN_LITERAL, "false", Position(3, 3))

        val stringExpr = nodeBuilder.buildLiteralExpressionNode(stringLiteral)
        val numberExpr = nodeBuilder.buildLiteralExpressionNode(numberLiteral)
        val booleanExpr = nodeBuilder.buildLiteralExpressionNode(booleanLiteral)

        assertEquals("hello", stringExpr.getValue())
        assertEquals(CommonTypes.STRING_LITERAL, stringExpr.getType())
        assertEquals(1, stringExpr.getCoordinates().getRow())

        assertEquals("3.14", numberExpr.getValue())
        assertEquals(CommonTypes.NUMBER_LITERAL, numberExpr.getType())
        assertEquals(2, numberExpr.getCoordinates().getRow())

        assertEquals("false", booleanExpr.getValue())
        assertEquals(CommonTypes.BOOLEAN_LITERAL, booleanExpr.getType())
        assertEquals(3, booleanExpr.getCoordinates().getRow())
    }

    @Test
    fun testIdentifierExpressionAllMethods() {
        val customIdentifier = PrintScriptToken(CommonTypes.IDENTIFIER, "customVar", Position(4, 8))
        val identifierExpr = nodeBuilder.buildIdentifierNode(customIdentifier)

        assertEquals("customVar", identifierExpr.getValue())
        assertEquals(customIdentifier, identifierExpr.getToken())
        assertEquals(4, identifierExpr.getCoordinates().getRow())
        assertEquals(8, identifierExpr.getCoordinates().getColumn())
    }

    @Test
    fun testAssignmentStatementAllMethods() {
        val customIdentifier = PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5))
        val value = nodeBuilder.buildLiteralExpressionNode(numberToken)

        val assignment = nodeBuilder.buildAssignmentStatementNode(customIdentifier, value)

        assertEquals("x", assignment.getIdentifier())
        assertEquals(value, assignment.getValue())
        assertEquals(customIdentifier, assignment.getIdentifierToken())
        assertEquals(value.getCoordinates(), assignment.getCoordinates())
    }

    @Test
    fun testDeclarationStatementWithAllDataTypes() {
        val numberDecl =
            nodeBuilder.buildVariableDeclarationStatementNode(
                declarationTypeToken,
                identifierToken,
                dataTypeToken,
                nodeBuilder.buildLiteralExpressionNode(numberToken),
            )

        val stringDecl =
            nodeBuilder.buildVariableDeclarationStatementNode(
                constToken,
                identifierToken,
                stringToken,
                nodeBuilder.buildLiteralExpressionNode(mockToken),
            )

        val booleanType = PrintScriptToken(CommonTypes.BOOLEAN, "BOOLEAN", Position(1, 1))
        val booleanDecl =
            nodeBuilder.buildVariableDeclarationStatementNode(
                declarationTypeToken,
                identifierToken,
                booleanType,
                nodeBuilder.buildLiteralExpressionNode(booleanToken),
            )

        assertEquals(CommonTypes.LET, numberDecl.getDeclarationType())
        assertEquals("myVar", numberDecl.getIdentifier())
        assertEquals(CommonTypes.NUMBER, numberDecl.getDataType())
        assertNotNull(numberDecl.getInitialValue())
        assertEquals(identifierToken, numberDecl.getIdentifierToken())

        assertEquals(CommonTypes.CONST, stringDecl.getDeclarationType())
        assertEquals(CommonTypes.STRING, stringDecl.getDataType())

        assertEquals(CommonTypes.BOOLEAN, booleanDecl.getDataType())
    }

    @Test
    fun testDeclarationStatementWithoutInitialValue() {
        val decl =
            nodeBuilder.buildVariableDeclarationStatementNode(
                declarationTypeToken,
                identifierToken,
                dataTypeToken,
                null,
            )

        assertEquals(CommonTypes.LET, decl.getDeclarationType())
        assertEquals("myVar", decl.getIdentifier())
        assertEquals(CommonTypes.NUMBER, decl.getDataType())
        assertNull(decl.getInitialValue())
        assertEquals(identifierToken, decl.getIdentifierToken())
    }

    @Test
    fun testIfStatementWithAlternative() {
        val condition = nodeBuilder.buildLiteralExpressionNode(booleanToken)
        val consequence =
            nodeBuilder.buildPrintStatementNode(
                nodeBuilder.buildLiteralExpressionNode(mockToken),
            )
        val alternative = nodeBuilder.buildEmptyStatementNode()

        val ifStmt = nodeBuilder.buildIfStatementNode(condition, consequence, alternative)

        assertEquals(condition, ifStmt.getCondition())
        assertEquals(consequence, ifStmt.getConsequence())
        assertEquals(alternative, ifStmt.getAlternative())
        assertTrue(ifStmt.hasAlternative())
        assertEquals(consequence.getCoordinates(), ifStmt.getCoordinates())
    }

    @Test
    fun testIfStatementWithoutAlternative() {
        val condition = nodeBuilder.buildLiteralExpressionNode(booleanToken)
        val consequence =
            nodeBuilder.buildPrintStatementNode(
                nodeBuilder.buildLiteralExpressionNode(mockToken),
            )

        val ifStmt = nodeBuilder.buildIfStatementNode(condition, consequence, null)

        assertEquals(condition, ifStmt.getCondition())
        assertEquals(consequence, ifStmt.getConsequence())
        assertNull(ifStmt.getAlternative())
        assertFalse(ifStmt.hasAlternative())
    }

    @Test
    fun testIfStatementDirectConstructor() {
        val condition = nodeBuilder.buildLiteralExpressionNode(booleanToken)
        val consequence = nodeBuilder.buildEmptyStatementNode()
        val customCoordinates = Position(5, 15)

        val ifStmt = IfStatement(condition, consequence, null, customCoordinates)

        assertEquals(condition, ifStmt.getCondition())
        assertEquals(consequence, ifStmt.getConsequence())
        assertNull(ifStmt.getAlternative())
        assertFalse(ifStmt.hasAlternative())
        assertEquals(5, ifStmt.getCoordinates().getRow())
        assertEquals(15, ifStmt.getCoordinates().getColumn())
    }

    @Test
    fun testExpressionStatementAllMethods() {
        val binaryExpr =
            nodeBuilder.buildBinaryExpressionNode(
                nodeBuilder.buildLiteralExpressionNode(numberToken),
                operatorToken,
                nodeBuilder.buildLiteralExpressionNode(numberToken),
            )

        val exprStmt = nodeBuilder.buildExpressionStatementNode(binaryExpr)

        assertEquals(binaryExpr, exprStmt.getExpression())
        assertEquals(binaryExpr.getCoordinates(), exprStmt.getCoordinates())
    }

    @Test
    fun testNestedBinaryExpressions() {
        val num1 =
            nodeBuilder.buildLiteralExpressionNode(
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "1", Position(1, 1)),
            )
        val num2 =
            nodeBuilder.buildLiteralExpressionNode(
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "2", Position(1, 5)),
            )
        val num3 =
            nodeBuilder.buildLiteralExpressionNode(
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "3", Position(1, 9)),
            )

        val plusOp = PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 3))
        val multiplyOp = PrintScriptToken(CommonTypes.OPERATORS, "*", Position(1, 7))

        val innerBinary = nodeBuilder.buildBinaryExpressionNode(num2, multiplyOp, num3)
        val outerBinary = nodeBuilder.buildBinaryExpressionNode(num1, plusOp, innerBinary)

        assertEquals(num1, outerBinary.getLeft())
        assertEquals("+", outerBinary.getOperator())
        assertEquals(innerBinary, outerBinary.getRight())

        val rightSide = outerBinary.getRight() as BinaryExpression
        assertEquals("2", (rightSide.getLeft() as LiteralExpression).getValue())
        assertEquals("*", rightSide.getOperator())
        assertEquals("3", (rightSide.getRight() as LiteralExpression).getValue())
    }

    @Test
    fun testComplexProgramStructure() {
        val varDecl =
            nodeBuilder.buildVariableDeclarationStatementNode(
                declarationTypeToken,
                PrintScriptToken(CommonTypes.IDENTIFIER, "result", Position(1, 5)),
                dataTypeToken,
                nodeBuilder.buildBinaryExpressionNode(
                    nodeBuilder.buildLiteralExpressionNode(
                        PrintScriptToken(CommonTypes.NUMBER_LITERAL, "10", Position(1, 15)),
                    ),
                    PrintScriptToken(CommonTypes.OPERATORS, "*", Position(1, 18)),
                    nodeBuilder.buildLiteralExpressionNode(
                        PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 20)),
                    ),
                ),
            )

        val condition =
            nodeBuilder.buildBinaryExpressionNode(
                nodeBuilder.buildIdentifierNode(
                    PrintScriptToken(CommonTypes.IDENTIFIER, "result", Position(2, 5)),
                ),
                PrintScriptToken(CommonTypes.COMPARISON, ">", Position(2, 12)),
                nodeBuilder.buildLiteralExpressionNode(
                    PrintScriptToken(CommonTypes.NUMBER_LITERAL, "0", Position(2, 14)),
                ),
            )

        val printStmt =
            nodeBuilder.buildPrintStatementNode(
                nodeBuilder.buildIdentifierNode(
                    PrintScriptToken(CommonTypes.IDENTIFIER, "result", Position(3, 11)),
                ),
            )

        val ifStmt = nodeBuilder.buildIfStatementNode(condition, printStmt, null)

        val program = nodeBuilder.buildProgramNode(listOf(varDecl, ifStmt))

        assertEquals(2, program.getStatements().size)

        val firstStmt = program.getStatements()[0] as DeclarationStatement
        assertEquals("result", firstStmt.getIdentifier())
        assertNotNull(firstStmt.getInitialValue())

        val secondStmt = program.getStatements()[1] as IfStatement
        assertNotNull(secondStmt.getCondition())
        assertNotNull(secondStmt.getConsequence())
        assertFalse(secondStmt.hasAlternative())
    }

    @Test
    fun testCoordinatesFromTokens() {
        val customCoords = Position(10, 20)
        val tokenWithCoords = PrintScriptToken(CommonTypes.IDENTIFIER, "test", customCoords)

        val identifierExpr = nodeBuilder.buildIdentifierNode(tokenWithCoords)
        val literalExpr = nodeBuilder.buildLiteralExpressionNode(tokenWithCoords)
        val assignment = nodeBuilder.buildAssignmentStatementNode(tokenWithCoords, literalExpr)

        assertEquals(10, identifierExpr.getCoordinates().getRow())
        assertEquals(20, identifierExpr.getCoordinates().getColumn())
        assertEquals(10, literalExpr.getCoordinates().getRow())
        assertEquals(20, literalExpr.getCoordinates().getColumn())
        assertEquals(literalExpr.getCoordinates(), assignment.getCoordinates())
    }

    @Test
    fun testEmptyProgramCoordinates() {
        val program = nodeBuilder.buildProgramNode(emptyList())

        assertEquals(0, program.getStatements().size)
        assertEquals(-1, program.getCoordinates().getRow())
        assertEquals(-1, program.getCoordinates().getColumn())
    }

    @Test
    fun testDefaultNodeBuilderCoordinateHandling() {
        val tokenCoords = Position(5, 10)
        val token = PrintScriptToken(CommonTypes.NUMBER_LITERAL, "42", tokenCoords)
        val expr = nodeBuilder.buildLiteralExpressionNode(token)

        assertEquals(tokenCoords, expr.getCoordinates())

        val binaryExpr =
            nodeBuilder.buildBinaryExpressionNode(
                expr,
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(5, 15)),
                expr,
            )

        assertEquals(5, binaryExpr.getCoordinates().getRow())
        assertEquals(15, binaryExpr.getCoordinates().getColumn())
    }

    @Test
    fun testDeclarationStatementEdgeCases() {
        val customCoords = Position(7, 14)
        val declType = PrintScriptToken(CommonTypes.CONST, "const", customCoords)
        val identifier = PrintScriptToken(CommonTypes.IDENTIFIER, "testVar", customCoords)
        val dataType = PrintScriptToken(CommonTypes.STRING, "STRING", customCoords)
        val initialValue = nodeBuilder.buildLiteralExpressionNode(mockToken)

        val decl = DeclarationStatement(declType, identifier, dataType, initialValue, customCoords)

        assertEquals(CommonTypes.CONST, decl.getDeclarationType())
        assertEquals("testVar", decl.getIdentifier())
        assertEquals(CommonTypes.STRING, decl.getDataType())
        assertEquals(initialValue, decl.getInitialValue())
        assertEquals(identifier, decl.getIdentifierToken())
        assertEquals(customCoords, decl.getCoordinates())
    }

    @Test
    fun testDeclarationStatementWithNullValue() {
        val customCoords = Position(8, 16)
        val decl =
            DeclarationStatement(
                declarationTypeToken,
                identifierToken,
                dataTypeToken,
                null,
                customCoords,
            )

        assertNull(decl.getInitialValue())
        assertEquals(customCoords, decl.getCoordinates())
    }

    @Test
    fun testBinaryExpressionDirectConstructor() {
        val left = nodeBuilder.buildLiteralExpressionNode(numberToken)
        val right = nodeBuilder.buildLiteralExpressionNode(numberToken)
        val customCoords = Position(9, 18)
        val operator = PrintScriptToken(CommonTypes.OPERATORS, "-", customCoords)

        val binary = BinaryExpression(left, operator, right, customCoords)

        assertEquals(left, binary.getLeft())
        assertEquals("-", binary.getOperator())
        assertEquals(right, binary.getRight())
        assertEquals(customCoords, binary.getCoordinates())
    }

    @Test
    fun testAssignmentStatementDirectConstructor() {
        val customCoords = Position(11, 22)
        val identifier = PrintScriptToken(CommonTypes.IDENTIFIER, "directTest", customCoords)
        val value = nodeBuilder.buildLiteralExpressionNode(numberToken)

        val assignment = AssignmentStatement(identifier, value, customCoords)

        assertEquals("directTest", assignment.getIdentifier())
        assertEquals(value, assignment.getValue())
        assertEquals(identifier, assignment.getIdentifierToken())
        assertEquals(customCoords, assignment.getCoordinates())
    }

    @Test
    fun testPrintStatementDirectConstructor() {
        val customCoords = Position(12, 24)
        val expression = nodeBuilder.buildLiteralExpressionNode(mockToken)

        val printStmt = PrintStatement(expression, customCoords)

        assertEquals(expression, printStmt.getExpression())
        assertNull(printStmt.getExpressionToken())
        assertEquals(customCoords, printStmt.getCoordinates())
    }

    @Test
    fun testPrintStatementWithEmptyExpression() {
        val emptyExpr = EmptyExpression()
        val printStmt = nodeBuilder.buildPrintStatementNode(emptyExpr)

        assertEquals(emptyExpr, printStmt.getExpression())
        assertNull(printStmt.getExpressionToken())
    }

    @Test
    fun testLiteralExpressionDirectConstructor() {
        val customCoords = Position(13, 26)
        val token = PrintScriptToken(CommonTypes.NUMBER_LITERAL, "999", customCoords)

        val literal = LiteralExpression(token, customCoords)

        assertEquals("999", literal.getValue())
        assertEquals(CommonTypes.NUMBER_LITERAL, literal.getType())
        assertEquals(customCoords, literal.getCoordinates())
    }

    @Test
    fun testIdentifierExpressionDirectConstructor() {
        val customCoords = Position(14, 28)
        val token = PrintScriptToken(CommonTypes.IDENTIFIER, "directId", customCoords)

        val identifier = IdentifierExpression(token, customCoords)

        assertEquals("directId", identifier.getValue())
        assertEquals(token, identifier.getToken())
        assertEquals(customCoords, identifier.getCoordinates())
    }

    @Test
    fun testExpressionStatementDirectConstructor() {
        val customCoords = Position(15, 30)
        val expr = nodeBuilder.buildLiteralExpressionNode(numberToken)

        val exprStmt = ExpressionStatement(expr, customCoords)

        assertEquals(expr, exprStmt.getExpression())
        assertEquals(customCoords, exprStmt.getCoordinates())
    }

    @Test
    fun testExpressionStatementWithDifferentExpressions() {
        val identifierExpr = nodeBuilder.buildIdentifierNode(identifierToken)
        val exprStmt1 = nodeBuilder.buildExpressionStatementNode(identifierExpr)
        assertEquals(identifierExpr, exprStmt1.getExpression())

        val binaryExpr =
            nodeBuilder.buildBinaryExpressionNode(
                nodeBuilder.buildLiteralExpressionNode(numberToken),
                operatorToken,
                nodeBuilder.buildLiteralExpressionNode(numberToken),
            )
        val exprStmt2 = nodeBuilder.buildExpressionStatementNode(binaryExpr)
        assertEquals(binaryExpr, exprStmt2.getExpression())

        val emptyExpr = EmptyExpression()
        val exprStmt3 = nodeBuilder.buildExpressionStatementNode(emptyExpr)
        assertEquals(emptyExpr, exprStmt3.getExpression())
    }

    @Test
    fun testProgramDirectConstructor() {
        val customCoords = Position(16, 32)
        val statements =
            listOf(
                nodeBuilder.buildEmptyStatementNode(),
                nodeBuilder.buildPrintStatementNode(
                    nodeBuilder.buildLiteralExpressionNode(mockToken),
                ),
            )

        val program = Program(statements, customCoords)

        assertEquals(statements, program.getStatements())
        assertEquals(customCoords, program.getCoordinates())
    }

    @Test
    fun testIfStatementWithComplexConditions() {
        val condition =
            nodeBuilder.buildBinaryExpressionNode(
                nodeBuilder.buildIdentifierNode(identifierToken),
                PrintScriptToken(CommonTypes.OPERATORS, "==", Position(1, 1)),
                nodeBuilder.buildLiteralExpressionNode(numberToken),
            )

        val consequence =
            nodeBuilder.buildPrintStatementNode(
                nodeBuilder.buildLiteralExpressionNode(mockToken),
            )
        val alternative =
            nodeBuilder.buildAssignmentStatementNode(
                identifierToken,
                nodeBuilder.buildLiteralExpressionNode(numberToken),
            )

        val ifStmt = nodeBuilder.buildIfStatementNode(condition, consequence, alternative)

        assertEquals(condition, ifStmt.getCondition())
        assertEquals(consequence, ifStmt.getConsequence())
        assertEquals(alternative, ifStmt.getAlternative())
        assertTrue(ifStmt.hasAlternative())
    }

    @Test
    fun testBinaryExpressionWithComparisonOperators() {
        val leftExpr = nodeBuilder.buildLiteralExpressionNode(numberToken)
        val rightExpr = nodeBuilder.buildLiteralExpressionNode(numberToken)

        val comparisonOperators = listOf("==", "!=", "<", ">", "<=", ">=")

        comparisonOperators.forEach { op ->
            val operator = PrintScriptToken(CommonTypes.COMPARISON, op, Position(1, 1))
            val binaryExpr = nodeBuilder.buildBinaryExpressionNode(leftExpr, operator, rightExpr)
            assertEquals(op, binaryExpr.getOperator())
        }
    }

    @Test
    fun testAllNodeTypesWithDefaultCoordinates() {
        val literal = LiteralExpression(mockToken, Position(1, 1))
        val identifier = IdentifierExpression(identifierToken, Position(1, 1))
        val binary =
            BinaryExpression(
                nodeBuilder.buildLiteralExpressionNode(numberToken),
                operatorToken,
                nodeBuilder.buildLiteralExpressionNode(numberToken),
                Position(1, 1),
            )
        val assignment =
            AssignmentStatement(
                identifierToken,
                nodeBuilder.buildLiteralExpressionNode(numberToken),
                Position(1, 1),
            )
        val declaration =
            DeclarationStatement(
                declarationTypeToken,
                identifierToken,
                dataTypeToken,
                null,
                Position(1, 1),
            )
        val printStmt =
            PrintStatement(nodeBuilder.buildLiteralExpressionNode(mockToken), Position(1, 1))
        val exprStmt =
            ExpressionStatement(nodeBuilder.buildLiteralExpressionNode(mockToken), Position(1, 1))
        val ifStmt =
            IfStatement(
                nodeBuilder.buildLiteralExpressionNode(booleanToken),
                nodeBuilder.buildEmptyStatementNode(),
                null,
                Position(1, 1),
            )
        val program = Program(emptyList())

        assertNotNull(literal.getCoordinates())
        assertNotNull(identifier.getCoordinates())
        assertNotNull(binary.getCoordinates())
        assertNotNull(assignment.getCoordinates())
        assertNotNull(declaration.getCoordinates())
        assertNotNull(printStmt.getCoordinates())
        assertNotNull(exprStmt.getCoordinates())
        assertNotNull(ifStmt.getCoordinates())
        assertNotNull(program.getCoordinates())
    }

    @Test
    fun `test EmptyExpression default coordinates`() {
        val emptyExpr = EmptyExpression()
        assertTrue(emptyExpr.getCoordinates() is UnassignedPosition)
        assertEquals("EmptyExpression", emptyExpr.toString())
    }

    @Test
    fun `test Program with empty statements`() {
        val program = Program(emptyList())
        assertTrue(program.getStatements().isEmpty())
        assertTrue(program.getCoordinates() is UnassignedPosition)
    }
}
