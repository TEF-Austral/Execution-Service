import builder.DefaultNodeBuilder
import coordinates.Position
import kotlin.collections.get
import node.AssignmentStatement
import node.BinaryExpression
import node.DeclarationStatement
import node.ExpressionStatement
import node.IdentifierExpression
import node.IfStatement
import node.LiteralExpression
import node.PrintStatement
import node.ReadEnvExpression
import node.ReadInputExpression
import parser.factory.VOnePointOneParserFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import type.CommonTypes

class ParserTest {

    @Test
    fun testSingleVariableDeclaration() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 7)),
                PrintScriptToken(CommonTypes.NUMBER, "NUMBER", Position(1, 9)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 16)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 18)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 19)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is DeclarationStatement)

        val varDecl = statement as DeclarationStatement
        assertEquals("x", varDecl.getIdentifier())
        assertEquals(CommonTypes.NUMBER, varDecl.getDataType())
        assertTrue(varDecl.getInitialValue() is LiteralExpression)

        val literal = varDecl.getInitialValue() as LiteralExpression
        assertEquals("5", literal.getValue())
    }

    @Test
    fun testMultipleStatementsWithBinaryExpression() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 7)),
                PrintScriptToken(CommonTypes.NUMBER, "NUMBER", Position(1, 9)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 16)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 18)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 19)),
                PrintScriptToken(CommonTypes.LET, "let", Position(2, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "y", Position(2, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(2, 7)),
                PrintScriptToken(CommonTypes.NUMBER, "NUMBER", Position(2, 9)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(2, 16)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(2, 18)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(2, 19)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(3, 1)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(3, 2)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "y", Position(3, 3)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(3, 4)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(3, program.getStatements().size)

        val firstStatement = program.getStatements()[0]
        assertTrue(firstStatement is DeclarationStatement)
        val firstVarDecl = firstStatement as DeclarationStatement
        assertEquals("x", firstVarDecl.getIdentifier())
        assertEquals(CommonTypes.NUMBER, firstVarDecl.getDataType())
        assertEquals("5", (firstVarDecl.getInitialValue() as LiteralExpression).getValue())

        val secondStatement = program.getStatements()[1]
        assertTrue(secondStatement is DeclarationStatement)
        val secondVarDecl = secondStatement as DeclarationStatement
        assertEquals("y", secondVarDecl.getIdentifier())
        assertEquals(CommonTypes.NUMBER, secondVarDecl.getDataType())
        assertEquals("5", (secondVarDecl.getInitialValue() as LiteralExpression).getValue())

        val thirdStatement = program.getStatements()[2]
        assertTrue(thirdStatement is ExpressionStatement)
        val exprStatement = thirdStatement as ExpressionStatement
        assertTrue(exprStatement.getExpression() is BinaryExpression)

        val binaryExpr = exprStatement.getExpression() as BinaryExpression
        assertTrue(binaryExpr.getLeft() is IdentifierExpression)
        assertTrue(binaryExpr.getRight() is IdentifierExpression)
        assertEquals("+", binaryExpr.getOperator())

        val leftIdentifier = binaryExpr.getLeft() as IdentifierExpression
        val rightIdentifier = binaryExpr.getRight() as IdentifierExpression
        assertEquals("x", leftIdentifier.getValue())
        assertEquals("y", rightIdentifier.getValue())
    }

    @Test
    fun testComplexExpressionWithParenthesesAndPrecedence() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 2)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 4)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 6)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 7)),
                PrintScriptToken(CommonTypes.OPERATORS, "*", Position(1, 9)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "2", Position(1, 11)),
                PrintScriptToken(CommonTypes.COMPARISON, ">", Position(1, 13)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "10", Position(1, 15)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 17)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is ExpressionStatement)

        val exprStatement = statement as ExpressionStatement
        val rootExpression = exprStatement.getExpression()
        assertTrue(rootExpression is BinaryExpression)

        val rootBinary = rootExpression as BinaryExpression

        assertEquals(">", rootBinary.getOperator())

        assertTrue(rootBinary.getRight() is LiteralExpression)
        val rightLiteral = rootBinary.getRight() as LiteralExpression
        assertEquals("10", rightLiteral.getValue())

        assertTrue(rootBinary.getLeft() is BinaryExpression)
        val leftMultiplication = rootBinary.getLeft() as BinaryExpression

        assertEquals("*", leftMultiplication.getOperator())

        assertTrue(leftMultiplication.getRight() is LiteralExpression)
        val multiplicationRight = leftMultiplication.getRight() as LiteralExpression
        assertEquals("2", multiplicationRight.getValue())

        assertTrue(leftMultiplication.getLeft() is BinaryExpression)
        val addition = leftMultiplication.getLeft() as BinaryExpression

        assertEquals("+", addition.getOperator())

        assertTrue(addition.getLeft() is IdentifierExpression)
        assertTrue(addition.getRight() is LiteralExpression)

        val identifier = addition.getLeft() as IdentifierExpression
        val additionRight = addition.getRight() as LiteralExpression

        assertEquals("x", identifier.getValue())
        assertEquals("5", additionRight.getValue())
    }

    @Test
    fun testExpressionWithMultiplePrecedenceLevels() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "2", Position(1, 1)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 3)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "3", Position(1, 5)),
                PrintScriptToken(CommonTypes.OPERATORS, "*", Position(1, 7)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "4", Position(1, 9)),
                PrintScriptToken(CommonTypes.COMPARISON, ">", Position(1, 11)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "10", Position(1, 14)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 16)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is ExpressionStatement)

        val exprStatement = statement as ExpressionStatement
        val rootExpression = exprStatement.getExpression()
        assertTrue(rootExpression is BinaryExpression)

        val rootBinary = rootExpression as BinaryExpression
        assertEquals(">", rootBinary.getOperator())

        assertTrue(rootBinary.getLeft() is BinaryExpression)
        val leftAddition = rootBinary.getLeft() as BinaryExpression
        assertEquals("+", leftAddition.getOperator())

        assertTrue(leftAddition.getRight() is BinaryExpression)
        val multiplication = leftAddition.getRight() as BinaryExpression
        assertEquals("*", multiplication.getOperator())

        val two = leftAddition.getLeft() as LiteralExpression
        val three = multiplication.getLeft() as LiteralExpression
        val four = multiplication.getRight() as LiteralExpression
        val ten = rootBinary.getRight() as LiteralExpression

        assertEquals("2", two.getValue())
        assertEquals("3", three.getValue())
        assertEquals("4", four.getValue())
        assertEquals("10", ten.getValue())
    }

    @Test
    fun testNestedParenthesesExpression() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 2)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 3)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 5)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "1", Position(1, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 8)),
                PrintScriptToken(CommonTypes.OPERATORS, "*", Position(1, 10)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 12)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "y", Position(1, 13)),
                PrintScriptToken(CommonTypes.OPERATORS, "-", Position(1, 15)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "2", Position(1, 17)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 18)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 19)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 20)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is ExpressionStatement)

        val exprStatement = statement as ExpressionStatement
        val rootExpression = exprStatement.getExpression()
        assertTrue(rootExpression is BinaryExpression)

        val rootBinary = rootExpression as BinaryExpression
        assertEquals("*", rootBinary.getOperator())

        assertTrue(rootBinary.getLeft() is BinaryExpression)
        assertTrue(rootBinary.getRight() is BinaryExpression)

        val leftAddition = rootBinary.getLeft() as BinaryExpression
        val rightSubtraction = rootBinary.getRight() as BinaryExpression

        assertEquals("+", leftAddition.getOperator())
        assertEquals("-", rightSubtraction.getOperator())

        val xIdentifier = leftAddition.getLeft() as IdentifierExpression
        val oneLiteral = leftAddition.getRight() as LiteralExpression
        val yIdentifier = rightSubtraction.getLeft() as IdentifierExpression
        val twoLiteral = rightSubtraction.getRight() as LiteralExpression

        assertEquals("x", xIdentifier.getValue())
        assertEquals("1", oneLiteral.getValue())
        assertEquals("y", yIdentifier.getValue())
        assertEquals("2", twoLiteral.getValue())
    }

    @Test
    fun simpleDeclaration() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 7)),
                PrintScriptToken(CommonTypes.NUMBER, "NUMBER", Position(1, 9)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 16)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is DeclarationStatement)

        val varDecl = statement as DeclarationStatement
        assertEquals("x", varDecl.getIdentifier())
        assertEquals(CommonTypes.NUMBER, varDecl.getDataType())
        assertNull(varDecl.getInitialValue())
    }

    @Test
    fun testSimplePrintStatement() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 6)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"Hello World\"", Position(1, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 21)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 22)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is PrintStatement)

        val printStatement = statement as PrintStatement
        assertTrue(printStatement.getExpression() is LiteralExpression)
        val literal = printStatement.getExpression() as LiteralExpression
        assertEquals("\"Hello World\"", literal.getValue())
    }

    @Test
    fun testPrintWithVariableExpression() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 6)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 8)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 9)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is PrintStatement)

        val printStatement = statement as PrintStatement
        assertTrue(printStatement.getExpression() is IdentifierExpression)
        val identifier = printStatement.getExpression() as IdentifierExpression
        assertEquals("x", identifier.getValue())
    }

    @Test
    fun testPrintWithComplexExpression() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 6)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 7)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 9)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 11)),
                PrintScriptToken(CommonTypes.OPERATORS, "*", Position(1, 13)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "2", Position(1, 15)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 16)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 17)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is PrintStatement)

        val printStatement = statement as PrintStatement
        assertTrue(printStatement.getExpression() is BinaryExpression)
        val binaryExpr = printStatement.getExpression() as BinaryExpression
        assertEquals("+", binaryExpr.getOperator())
    }

    @Test
    fun testSimpleAssignmentStatement() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 1)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 3)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "10", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 7)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is AssignmentStatement)

        val assignmentStatement = statement as AssignmentStatement
        assertEquals("x", assignmentStatement.getIdentifier())
        assertTrue(assignmentStatement.getValue() is LiteralExpression)
        val literal = assignmentStatement.getValue() as LiteralExpression
        assertEquals("10", literal.getValue())
    }

    @Test
    fun testAssignmentWithStringLiteral() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.IDENTIFIER, "name", Position(1, 1)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 6)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"John\"", Position(1, 8)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 14)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is AssignmentStatement)

        val assignmentStatement = statement as AssignmentStatement
        assertEquals("name", assignmentStatement.getIdentifier())
        assertTrue(assignmentStatement.getValue() is LiteralExpression)
        val literal = assignmentStatement.getValue() as LiteralExpression
        assertEquals("\"John\"", literal.getValue())
    }

    @Test
    fun testAssignmentWithExpression() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.IDENTIFIER, "result", Position(1, 1)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 8)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 10)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 12)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "y", Position(1, 14)),
                PrintScriptToken(CommonTypes.OPERATORS, "*", Position(1, 16)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "3", Position(1, 18)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 19)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is AssignmentStatement)

        val assignmentStatement = statement as AssignmentStatement
        assertEquals("result", assignmentStatement.getIdentifier())
        assertTrue(assignmentStatement.getValue() is BinaryExpression)

        val binaryExpr = assignmentStatement.getValue() as BinaryExpression
        assertEquals("+", binaryExpr.getOperator())
    }

    @Test
    fun testDeclarationAssignmentAndPrint() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 6)),
                PrintScriptToken(CommonTypes.NUMBER, "NUMBER", Position(1, 8)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 15)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 17)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 18)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(2, 1)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(2, 3)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "10", Position(2, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(2, 7)),
                PrintScriptToken(CommonTypes.PRINT, "print", Position(3, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(3, 6)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(3, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(3, 8)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(3, 9)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(3, program.getStatements().size)

        assertTrue(program.getStatements()[0] is DeclarationStatement)
        val declaration = program.getStatements()[0] as DeclarationStatement
        assertEquals("x", declaration.getIdentifier())
        assertEquals(CommonTypes.NUMBER, declaration.getDataType())

        assertTrue(program.getStatements()[1] is AssignmentStatement)
        val assignment = program.getStatements()[1] as AssignmentStatement
        assertEquals("x", assignment.getIdentifier())

        assertTrue(program.getStatements()[2] is PrintStatement)
        val print = program.getStatements()[2] as PrintStatement
        assertTrue(print.getExpression() is IdentifierExpression)
    }

    @Test
    fun testDeclarationWithInvalidDataType() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 6)),
                PrintScriptToken(
                    CommonTypes.IDENTIFIER,
                    "INVALID_TYPE",
                    Position(1, 8),
                ),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 20)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()
        assertTrue(!result.isSuccess() || result.message().contains("error"))
    }

    @Test
    fun testStringDeclarationWithValidType() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "name", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 10)),
                PrintScriptToken(CommonTypes.STRING, "STRING", Position(1, 12)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 19)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"John\"", Position(1, 21)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 27)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is DeclarationStatement)

        val varDecl = statement as DeclarationStatement
        assertEquals("name", varDecl.getIdentifier())
        assertEquals(CommonTypes.STRING, varDecl.getDataType())
        assertTrue(varDecl.getInitialValue() is LiteralExpression)
    }

    @Test
    fun testPrintStatementWithBinaryExpression() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 6)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 7)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 9)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 10)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 11)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertTrue(result.isSuccess())
    }

    @Test
    fun testAssignmentWithPositiveExpression() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 1)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 3)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 5)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 8)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertTrue(result.isSuccess())
    }

    @Test
    fun testConstDeclaration() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.CONST, "const", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "PI", Position(1, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 9)),
                PrintScriptToken(CommonTypes.NUMBER, "NUMBER", Position(1, 11)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 18)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "3.14", Position(1, 20)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 24)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is DeclarationStatement)

        val constDecl = statement as DeclarationStatement
        assertEquals("PI", constDecl.getIdentifier())
        assertEquals(CommonTypes.NUMBER, constDecl.getDataType())
        assertTrue(constDecl.getInitialValue() is LiteralExpression)
        val literal = constDecl.getInitialValue() as LiteralExpression
        assertEquals("3.14", literal.getValue())
    }

    @Test
    fun testBooleanDeclaration() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "isTrue", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 11)),
                PrintScriptToken(CommonTypes.BOOLEAN, "BOOLEAN", Position(1, 13)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 21)),
                PrintScriptToken(CommonTypes.BOOLEAN_LITERAL, "true", Position(1, 23)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 27)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is DeclarationStatement)

        val boolDecl = statement as DeclarationStatement
        assertEquals("isTrue", boolDecl.getIdentifier())
        assertEquals(CommonTypes.BOOLEAN, boolDecl.getDataType())
        assertTrue(boolDecl.getInitialValue() is LiteralExpression)
        val literal = boolDecl.getInitialValue() as LiteralExpression
        assertEquals("true", literal.getValue())
    }

    @Test
    fun testBooleanAssignment() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.IDENTIFIER, "isActive", Position(1, 1)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 10)),
                PrintScriptToken(CommonTypes.BOOLEAN_LITERAL, "false", Position(1, 12)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 17)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is AssignmentStatement)

        val assignment = statement as AssignmentStatement
        assertEquals("isActive", assignment.getIdentifier())
        assertTrue(assignment.getValue() is LiteralExpression)
        val literal = assignment.getValue() as LiteralExpression
        assertEquals("false", literal.getValue())
    }

    @Test
    fun testSimpleIfStatement() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.CONDITIONALS, "if", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 4)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.COMPARISON, ">", Position(1, 7)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "0", Position(1, 9)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 10)),
                PrintScriptToken(CommonTypes.DELIMITERS, "{", Position(1, 12)),
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 13)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 18)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"positive\"", Position(1, 19)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 29)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 30)),
                PrintScriptToken(CommonTypes.DELIMITERS, "}", Position(1, 31)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is IfStatement)

        val ifStatement = statement as IfStatement
        assertTrue(ifStatement.getCondition() is BinaryExpression)
        assertTrue(ifStatement.getConsequence() is PrintStatement)
        assertTrue(ifStatement.getAlternative() == null)
    }

    @Test
    fun testIfElseStatement() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.CONDITIONALS, "if", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 4)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.COMPARISON, ">", Position(1, 7)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "0", Position(1, 9)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 10)),
                PrintScriptToken(CommonTypes.DELIMITERS, "{", Position(1, 12)),
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 13)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 18)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"positive\"", Position(1, 19)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 29)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 30)),
                PrintScriptToken(CommonTypes.DELIMITERS, "}", Position(1, 31)),
                PrintScriptToken(CommonTypes.CONDITIONALS, "else", Position(1, 33)),
                PrintScriptToken(CommonTypes.DELIMITERS, "{", Position(1, 38)),
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 39)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 44)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"negative\"", Position(1, 45)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 55)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 56)),
                PrintScriptToken(CommonTypes.DELIMITERS, "}", Position(1, 57)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is IfStatement)

        val ifStatement = statement as IfStatement
        assertTrue(ifStatement.getCondition() is BinaryExpression)
        assertTrue(ifStatement.getConsequence() is PrintStatement)
        assertTrue(ifStatement.getAlternative() is PrintStatement)
    }

    @Test
    fun testElseIfStatement() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.CONDITIONALS, "if", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 4)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.COMPARISON, ">", Position(1, 7)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "10", Position(1, 9)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 11)),
                PrintScriptToken(CommonTypes.DELIMITERS, "{", Position(1, 13)),
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 14)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 19)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"BIG\"", Position(1, 20)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 25)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 26)),
                PrintScriptToken(CommonTypes.DELIMITERS, "}", Position(1, 27)),
                PrintScriptToken(CommonTypes.CONDITIONALS, "else", Position(1, 29)),
                PrintScriptToken(CommonTypes.CONDITIONALS, "if", Position(1, 34)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 37)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 38)),
                PrintScriptToken(CommonTypes.COMPARISON, ">", Position(1, 40)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "0", Position(1, 42)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 43)),
                PrintScriptToken(CommonTypes.DELIMITERS, "{", Position(1, 45)),
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 46)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 51)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"small\"", Position(1, 52)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 59)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 60)),
                PrintScriptToken(CommonTypes.DELIMITERS, "}", Position(1, 61)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()
        val program = result.getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is IfStatement)

        val ifStatement = statement as IfStatement
        assertTrue(ifStatement.getCondition() is BinaryExpression)
        assertTrue(ifStatement.getConsequence() is PrintStatement)
        assertTrue(ifStatement.getAlternative() is IfStatement)
    }

    @Test
    fun testIfStatementWithBooleanCondition() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.CONDITIONALS, "if", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 4)),
                PrintScriptToken(CommonTypes.BOOLEAN_LITERAL, "true", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 9)),
                PrintScriptToken(CommonTypes.DELIMITERS, "{", Position(1, 11)),
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 12)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 17)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"always\"", Position(1, 18)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 26)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 27)),
                PrintScriptToken(CommonTypes.DELIMITERS, "}", Position(1, 28)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is IfStatement)

        val ifStatement = statement as IfStatement
        assertTrue(ifStatement.getCondition() is LiteralExpression)
        val condition = ifStatement.getCondition() as LiteralExpression
        assertEquals("true", condition.getValue())
    }

    @Test
    fun testConstDeclarationWithoutExplicitType() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.CONST, "const", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "message", Position(1, 7)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 15)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"Hello World\"", Position(1, 17)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 30)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is DeclarationStatement)

        val constDecl = statement as DeclarationStatement
        assertEquals("message", constDecl.getIdentifier())
        assertTrue(constDecl.getInitialValue() is LiteralExpression)
        val literal = constDecl.getInitialValue() as LiteralExpression
        assertEquals("\"Hello World\"", literal.getValue())
    }

    @Test
    fun testIdentifierAssignmentIdentifier() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.IDENTIFIER, "isActive", Position(1, 1)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 10)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "yes", Position(1, 12)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 15)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertTrue(result.isSuccess())
    }

    @Test
    fun testConstDeclarationWithoutTypeNumberInference() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.CONST, "const", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "count", Position(1, 7)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 13)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "42", Position(1, 15)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 17)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is DeclarationStatement)

        val constDecl = statement as DeclarationStatement
        assertEquals("count", constDecl.getIdentifier())
        assertTrue(constDecl.getInitialValue() is LiteralExpression)
        val literal = constDecl.getInitialValue() as LiteralExpression
        assertEquals("42", literal.getValue())
    }

    @Test
    fun testConstDeclarationWithoutTypeBooleanInference() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.CONST, "const", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "isReady", Position(1, 7)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 15)),
                PrintScriptToken(CommonTypes.BOOLEAN_LITERAL, "true", Position(1, 17)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 21)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val statement = program.getStatements()[0]
        assertTrue(statement is DeclarationStatement)

        val constDecl = statement as DeclarationStatement
        assertEquals("isReady", constDecl.getIdentifier())
        assertTrue(constDecl.getInitialValue() is LiteralExpression)
        val literal = constDecl.getInitialValue() as LiteralExpression
        assertEquals("true", literal.getValue())
    }

    @Test
    fun `readEnv with literal works`() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.READ_ENV, "readEnv", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 8)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"USER\"", Position(1, 9)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 15)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 16)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val stmt = program.getStatements()[0] as ExpressionStatement
        assertEquals(1, stmt.getCoordinates().getRow())
        assertEquals(9, stmt.getCoordinates().getColumn())
        val value = (stmt.getExpression() as ReadEnvExpression).envName()
        assertEquals("\"USER\"", value)
    }

    @Test
    fun `readInput with literal works`() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.READ_INPUT, "readInput", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 10)),
                PrintScriptToken(
                    CommonTypes.STRING_LITERAL,
                    "\"Enter your name\"",
                    Position(1, 11),
                ),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 31)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 32)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(1, program.getStatements().size)
        val stmt = program.getStatements()[0] as ExpressionStatement
        assertEquals(1, stmt.getCoordinates().getRow())
        assertEquals(11, stmt.getCoordinates().getColumn())
        val value = (stmt.getExpression() as ReadInputExpression).printValue() as LiteralExpression
        assertEquals("\"Enter your name\"", value.getValue())
    }

    @Test
    fun `test declaration assignment and print sequence`() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 6)),
                PrintScriptToken(CommonTypes.NUMBER, "number", Position(1, 8)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 15)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "42", Position(1, 17)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 19)),
                // x = x + 1;
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(2, 1)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(2, 3)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(2, 5)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(2, 7)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "1", Position(2, 9)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(2, 10)),
                // print(x);
                PrintScriptToken(CommonTypes.PRINT, "print", Position(3, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(3, 6)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(3, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(3, 8)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(3, 9)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val program = parser.parse().getProgram()

        assertEquals(3, program.getStatements().size)

        // Check DeclarationStatement
        val declarationStatement = program.getStatements()[0]
        assertTrue(declarationStatement is DeclarationStatement)
        val declaration = declarationStatement as DeclarationStatement
        assertEquals("x", declaration.getIdentifier())
        assertEquals(CommonTypes.NUMBER, declaration.getDataType())
        val declValue = declaration.getInitialValue() as LiteralExpression
        assertEquals("42", declValue.getValue())

        // Check AssignmentStatement
        val assignmentStatement = program.getStatements()[1]
        assertTrue(assignmentStatement is AssignmentStatement)
        val assignment = assignmentStatement as AssignmentStatement
        assertEquals("x", assignment.getIdentifier())
        assertTrue(assignment.getValue() is BinaryExpression)
        val binaryExpr = assignment.getValue() as BinaryExpression
        assertEquals("+", binaryExpr.getOperator())
        val left = binaryExpr.getLeft() as IdentifierExpression
        assertEquals("x", left.getValue())
        val right = binaryExpr.getRight() as LiteralExpression
        assertEquals("1", right.getValue())

        // Check PrintStatement
        val printStatement = program.getStatements()[2]
        assertTrue(printStatement is PrintStatement)
        val print = printStatement as PrintStatement
        val printExpr = print.getExpression() as IdentifierExpression
        assertEquals("x", printExpr.getValue())
    }

    @Test
    fun `readInput with identifier`() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.READ_INPUT, "readInput", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 10)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "msg", Position(1, 11)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 14)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 15)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertTrue(result.isSuccess())
    }

    @Test
    fun `readInput with complex expression`() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.READ_INPUT, "readInput", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 10)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"a\"", Position(1, 11)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 14)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"b\"", Position(1, 16)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 19)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 20)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertTrue(result.isSuccess())
    }
}
