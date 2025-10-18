import builder.DefaultNodeBuilder
import coordinates.Position
import node.DeclarationStatement
import node.EmptyExpression
import node.LiteralExpression
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

import org.junit.jupiter.api.Test
import parser.factory.VOnePointOneParserFactory
import parser.result.NextResult
import type.CommonTypes

class PullParserTest {

    private val factory = VOnePointOneParserFactory()

    @Test
    fun `test next should parse a single valid statement successfully`() {
        // Setup: Creamos una lista de tokens para: let name: string = "test";
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "name", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 9)),
                PrintScriptToken(CommonTypes.STRING, "string", Position(1, 11)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 18)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "test", Position(1, 20)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 26)),
            )
        val parser = factory.createParser(MockTokenStream(tokens), DefaultNodeBuilder())

        // Act: Llamamos a next()
        val result: NextResult = parser.next()

        assertTrue(result.isSuccess, "The parsing should be successful")
        assertTrue(
            result.node is DeclarationStatement,
            "The parsed statement should be a DeclarationNode",
        )

        val declaration = result.node as DeclarationStatement
        assertEquals("name", (declaration.getIdentifier()))
        assertEquals("test", (declaration.getInitialValue() as LiteralExpression).getValue())

        val nextParser = result.parser
        assertFalse(nextParser.hasNext(), "The parser should be at the end of the stream")
    }

    @Test
    fun `test next should fail gracefully when no tokens are available`() {
        val tokens = emptyList<Token>()
        val parser = factory.createParser(MockTokenStream(tokens), DefaultNodeBuilder())

        val result = parser.next()

        assertFalse(result.isSuccess, "Parsing should fail on an empty stream")
        assertTrue(result.node is EmptyExpression, "Node should be EmptyExpression on failure")
        assertEquals("No more tokens available", result.message)
    }

    @Test
    fun `test next should parse multiple statements one by one`() {
        // Setup: Tokens para dos sentencias: let a: number = 1; let b: string = "b";
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "a", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 6)),
                PrintScriptToken(CommonTypes.NUMBER, "number", Position(1, 8)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 15)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "1", Position(1, 17)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 18)),
                PrintScriptToken(CommonTypes.LET, "let", Position(2, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "b", Position(2, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(2, 6)),
                PrintScriptToken(CommonTypes.STRING, "string", Position(2, 8)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(2, 15)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"b\"", Position(2, 17)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(2, 20)),
            )
        val parser = factory.createParser(MockTokenStream(tokens), DefaultNodeBuilder())

        val firstResult = parser.next()
        assertTrue(firstResult.isSuccess, "First next() call should be successful")
        val firstNode = firstResult.node as DeclarationStatement
        assertEquals("a", (firstNode.getIdentifier()))

        val newParser = firstResult.parser
        assertTrue(newParser.hasNext(), "Parser should have tokens for the second statement")

        val secondResult = newParser.next()
        assertTrue(secondResult.isSuccess, "Second next() call should be successful")
        val secondNode = secondResult.node as DeclarationStatement
        assertEquals("b", (secondNode.getIdentifier()))

        val finalParser = secondResult.parser
        assertFalse(finalParser.hasNext(), "Parser should be at the end after the second call")
    }
}
