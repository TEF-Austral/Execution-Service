import builder.DefaultNodeBuilder
import coordinates.Position

import stream.AstStream
import kotlin.test.assertEquals
import node.DeclarationStatement
import node.PrintStatement
import node.EmptyExpression
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import parser.factory.VOnePointOneParserFactory
import parser.stream.ParserAstStream
import type.CommonTypes

class ParserAstStreamNoMockTest {

    private val nodeBuilder = DefaultNodeBuilder()
    private val parserFactory = VOnePointOneParserFactory()

    @Test
    fun `test next() should return the next node and a new stream`() {
        // Tokens para dos sentencias: let x: NUMBER = 5; print("hello");
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "x", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 7)),
                PrintScriptToken(CommonTypes.NUMBER, "NUMBER", Position(1, 9)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 16)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "5", Position(1, 18)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 19)),
                PrintScriptToken(CommonTypes.PRINT, "print", Position(2, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(2, 6)),
                PrintScriptToken(CommonTypes.STRING_LITERAL, "\"hello\"", Position(2, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(2, 14)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(2, 15)),
            )

        val parser = parserFactory.createParser(MockTokenStream(tokens), nodeBuilder)
        val astStream: AstStream = ParserAstStream(parser)

        val result1 = astStream.next()
        val node1 = result1.node
        val nextStream1 = result1.nextStream

        assertTrue(node1 is DeclarationStatement)
        assertEquals("x", (node1 as DeclarationStatement).getIdentifier())
        assertFalse(
            nextStream1.isAtEnd(),
            "El stream no debería estar al final después del primer next()",
        )

        // Segunda llamada a next() sobre el nuevo stream
        val result2 = nextStream1.next()
        val node2 = result2.node
        val nextStream2 = result2.nextStream

        assertTrue(node2 is PrintStatement)
        assertTrue(
            nextStream2.isAtEnd(),
            "El stream debería estar al final después del segundo next()",
        )
    }

    @Test
    fun `test peak() should return the next node without consuming it`() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.IDENTIFIER, "a", Position(1, 5)),
                PrintScriptToken(CommonTypes.DELIMITERS, ":", Position(1, 7)),
                PrintScriptToken(CommonTypes.STRING, "STRING", Position(1, 9)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 15)),
            )

        val parser = parserFactory.createParser(MockTokenStream(tokens), nodeBuilder)
        val astStream: AstStream = ParserAstStream(parser)

        // Llamar a peak() múltiples veces
        val nodeFromPeak1 = astStream.peak()
        val nodeFromPeak2 = astStream.peak()

        assertNotNull(nodeFromPeak1)
        assertTrue(nodeFromPeak1 is DeclarationStatement)

        val result = astStream.next()
        val nodeFromNext = result.node

        assertTrue(nodeFromNext is DeclarationStatement)
    }

    @Test
    fun `test isAtEnd() should return true only when the stream is fully consumed`() {
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.PRINT, "print", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, "(", Position(1, 6)),
                PrintScriptToken(CommonTypes.NUMBER_LITERAL, "123", Position(1, 7)),
                PrintScriptToken(CommonTypes.DELIMITERS, ")", Position(1, 10)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 11)),
            )
        val parser = parserFactory.createParser(MockTokenStream(tokens), nodeBuilder)
        val astStream: AstStream = ParserAstStream(parser)

        assertFalse(astStream.isAtEnd(), "El stream inicial no debería estar al final")

        val result = astStream.next()

        assertFalse(astStream.isAtEnd(), "El stream original no debe cambiar su estado (inmutable)")
        assertTrue(
            result.nextStream.isAtEnd(),
            "El nuevo stream devuelto por next() debería estar al final",
        )
    }

    @Test
    fun `test next() on an empty stream should return failure result`() {
        val tokens = emptyList<PrintScriptToken>()
        val parser = parserFactory.createParser(MockTokenStream(tokens), nodeBuilder)
        val astStream: AstStream = ParserAstStream(parser)

        assertTrue(astStream.isAtEnd())

        val result = astStream.next()
        assertFalse(result.isSuccess)
        assertTrue(result.node is EmptyExpression)
        assertTrue(result.nextStream.isAtEnd())
    }

    @Test
    fun `test peak() on an empty stream should return EmptyExpression`() {
        val tokens = emptyList<PrintScriptToken>()
        val parser = parserFactory.createParser(MockTokenStream(tokens), nodeBuilder)
        val astStream: AstStream = ParserAstStream(parser)

        assertTrue(astStream.isAtEnd())

        val node = astStream.peak()
        assertTrue(node is EmptyExpression)
    }

    @Test
    fun `test next() with invalid syntax should throw Exception`() {
        // "let ;" es sintaxis inválida
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.DELIMITERS, ";", Position(1, 5)),
            )

        val parser = parserFactory.createParser(MockTokenStream(tokens), nodeBuilder)
        val astStream: AstStream = ParserAstStream(parser)

        val exception = astStream.next()
        assertFalse(exception.isSuccess)
    }

    @Test
    fun `test peak() with invalid syntax should return EmptyExpression`() {
        // "let =" es sintaxis inválida
        val tokens =
            listOf(
                PrintScriptToken(CommonTypes.LET, "let", Position(1, 1)),
                PrintScriptToken(CommonTypes.ASSIGNMENT, "=", Position(1, 5)),
            )

        val parser = parserFactory.createParser(MockTokenStream(tokens), nodeBuilder)
        val astStream: AstStream = ParserAstStream(parser)

        val node = astStream.peak()
        assertTrue(node is EmptyExpression)
    }
}
