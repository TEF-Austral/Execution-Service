import builder.DefaultNodeBuilder
import coordinates.Position
import parser.factory.VOnePointOneParserFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

import type.CommonTypes

class ParserErrorTest {

    @Test
    fun testInvalidVariableDeclarationMissingType() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 7)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 9)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 10)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Can't be handled currently by the statement parser", result.message())
    }

    @Test
    fun testInvalidVariableDeclarationMissingIdentifier() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 5)),
                PrintScriptToken(CommonTypes.NUMBER, "NUMBER", Position(1, 7)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 14)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 16)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 17)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Can't be handled currently by the statement parser", result.message())
    }

    @Test
    fun testInvalidExpressionMissingOperand() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 1)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 3)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 5)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Invalid structure", result.message())
    }

    @Test
    fun testInvalidExpressionUnmatchedParentheses() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 1)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 2)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 4)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "3", Position(1, 6)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 7)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Expected closing parenthesis ')'", result.message())
    }

    @Test
    fun testInvalidVariableDeclarationMissingSemicolon() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 7)),
                PrintScriptToken(CommonTypes.NUMBER, "NUMBER", Position(1, 9)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 16)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 18)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Can't be handled currently by the statement parser", result.message())
    }

    @Test
    fun testInvalidExpressionExtraClosingParenthesis() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 1)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 3)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "3", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 6)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 7)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Can't be handled currently by the statement parser", result.message())
    }

    @Test
    fun testInvalidExpressionConsecutiveOperators() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 1)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 3)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 5)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "3", Position(1, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 8)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Can't put operators side by side", result.message())
    }

    @Test
    fun testInvalidTokenSequenceEmptyParentheses() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 2)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 3)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Invalid structure", result.message())
    }

    @Test
    fun testInvalidVariableDeclarationWrongOrder() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.NUMBER, "NUMBER", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 8)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 10)),
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 12)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 16)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 18)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 19)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Can't be handled currently by the statement parser", result.message())
    }

    @Test
    fun testUnexpectedTokenAfterValidStatement() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 7)),
                PrintScriptToken(CommonTypes.NUMBER, "NUMBER", Position(1, 9)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 16)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 18)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 19)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(2, 1)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Exceeded parsing limits", result.message())
    }

    @Test
    fun testEmptyTokenList() {
        val tokens = emptyList<PrintScriptToken>()

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertTrue(result.isSuccess())
        assertEquals(0, result.getProgram().getStatements().size)
    }

    @Test
    fun testPrintStatementMissingOpeningParenthesis() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 1)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"Hello World\"", Position(1, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 21)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertTrue(result.message().contains("parenthesis") || result.message().contains("("))
    }

    @Test
    fun testPrintStatementMissingClosingParenthesis() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 6)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"Hello World\"", Position(1, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 21)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertTrue(result.message().contains("parenthesis") || result.message().contains(")"))
    }

    @Test
    fun testPrintStatementMissingSemicolon() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 6)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"Hello World\"", Position(1, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 21)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Was expecting a semicolon", result.message())
    }

    @Test
    fun testPrintStatementEmptyParentheses() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 6)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 8)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Was expecting closing parenthesis", result.message())
    }

    @Test
    fun testAssignmentStatementMissingIdentifier() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 1)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "10", Position(1, 3)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 5)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Can't be handled currently by the statement parser", result.message())
    }

    @Test
    fun testAssignmentStatementMissingAssignmentOperator() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 1)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "10", Position(1, 3)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 5)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Invalid structure", result.message())
    }

    @Test
    fun testAssignmentStatementMissingValue() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 1)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 3)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 5)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Invalid structure", result.message())
    }

    @Test
    fun testAssignmentWithUnmatchedParentheses() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 1)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 3)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 5)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 6)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 8)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "3", Position(1, 10)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 11)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertTrue(result.message().contains("parenthesis") || result.message().contains(")"))
    }

    @Test
    fun testDeclarationWithUnknownDataType() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 6)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "UNKNOWN_TYPE", Position(1, 8)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 20)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
    }

    @Test
    fun testDeclarationWithNumberLiteralAsType() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 6)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "123", Position(1, 8)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 11)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
    }

    @Test
    fun testDeclarationWithStringLiteralAsType() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 6)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"STRING\"", Position(1, 8)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 16)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
    }

    @Test
    fun testDeclarationWithOperatorAsType() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 6)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 8)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 9)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
    }

    @Test
    fun testMultipleErrorsInSequence() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 6)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 7)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 9)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 11)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
    }

    @Test
    fun testPrintWithAssignmentInsteadOfExpression() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 6)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 7)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 9)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 11)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 12)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 13)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()
        assertFalse(result.isSuccess())
        assertEquals("Was expecting closing parenthesis", result.message())
    }

    @Test
    fun testInvalidExpressionMissingOperator() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 1)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "3", Position(1, 3)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 4)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Invalid structure", result.message())
    }

    @Test
    fun testIfStatementMissingOpeningParenthesis() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.CONDITIONALS, "if", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 4)),
                PrintScriptToken(CommonTypes.COMPARISON, ">", Position(1, 6)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "0", Position(1, 8)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 9)),
                PrintScriptToken(CommonTypes.DELIMITERS, "{", Position(1, 11)),
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 12)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 17)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"test\"", Position(1, 18)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 24)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 25)),
                PrintScriptToken(CommonTypes.DELIMITERS, "}", Position(1, 26)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertTrue(result.message().contains("(") || result.message().contains("parenthesis"))
    }

    @Test
    fun testIfStatementMissingClosingParenthesis() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.CONDITIONALS, "if", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 4)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.COMPARISON, ">", Position(1, 7)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "0", Position(1, 9)),
                PrintScriptToken(CommonTypes.DELIMITERS, "{", Position(1, 11)),
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 12)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 17)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"test\"", Position(1, 18)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 24)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 25)),
                PrintScriptToken(CommonTypes.DELIMITERS, "}", Position(1, 26)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertTrue(result.message().contains(")") || result.message().contains("parenthesis"))
    }

    @Test
    fun testIfStatementMissingCondition() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.CONDITIONALS, "if", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 4)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, "{", Position(1, 7)),
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 8)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 13)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"test\"", Position(1, 14)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 20)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 21)),
                PrintScriptToken(CommonTypes.DELIMITERS, "}", Position(1, 22)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertTrue(
            result.message().contains("condition") || result.message().contains("expression"),
        )
    }

    @Test
    fun testIfStatementMissingThenStatement() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.CONDITIONALS, "if", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 4)),
                PrintScriptToken(CommonTypes.BOOLEAN_LITERAL, "true", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 9)),
                PrintScriptToken(CommonTypes.DELIMITERS, "{", Position(1, 11)),
                PrintScriptToken(CommonTypes.DELIMITERS, "}", Position(1, 12)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
    }

    @Test
    fun testElseWithoutIf() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.CONDITIONALS, "else", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "{", Position(1, 6)),
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 12)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"else\"", Position(1, 13)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 19)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 20)),
                PrintScriptToken(CommonTypes.DELIMITERS, "}", Position(1, 21)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Can't be handled currently by the statement parser", result.message())
    }

    @Test
    fun testConstDeclarationWithoutInitialValue() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.CONST, "const", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "PI", Position(1, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 9)),
                PrintScriptToken(CommonTypes.NUMBER, "NUMBER", Position(1, 11)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 18)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
    }

    @Test
    fun testConstDeclarationMissingAssignment() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.CONST, "const", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "PI", Position(1, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 9)),
                PrintScriptToken(CommonTypes.NUMBER, "NUMBER", Position(1, 11)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "3.14", Position(1, 18)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 22)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
    }

    @Test
    fun testBooleanDeclarationWithInvalidValue() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "isTrue", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 11)),
                PrintScriptToken(CommonTypes.BOOLEAN, "BOOLEAN", Position(1, 13)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 21)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"true\"", Position(1, 23)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 29)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertTrue(result.isSuccess())
    }

    @Test
    fun testBooleanDeclarationWithNumberLiteral() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "isTrue", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 11)),
                PrintScriptToken(CommonTypes.BOOLEAN, "BOOLEAN", Position(1, 13)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 21)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "1", Position(1, 23)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 24)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertTrue(result.isSuccess())
    }

    @Test
    fun `readEnv with identifier fails`() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.READ_ENV, "readEnv", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 8)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 9)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 10)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 11)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Only a literal expression can be inside of readEnv", result.message())
    }

    @Test
    fun `readEnv with complex expression fails`() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.READ_ENV, "readEnv", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 8)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 9)),
                PrintScriptToken(CommonTypes.OPERATORS, "+", Position(1, 11)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 13)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 14)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 15)),
            )

        val nodeBuilder = DefaultNodeBuilder()
        val parser = VOnePointOneParserFactory().createParser(MockTokenStream(tokens), nodeBuilder)
        val result = parser.parse()

        assertFalse(result.isSuccess())
        assertEquals("Only a literal expression can be inside of readEnv", result.message())
    }
}
