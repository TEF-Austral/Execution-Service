package stream.token
import Lexer
import Token
import TokenStream
import result.StreamResult

class LexerTokenStream(
    private val lexer: Lexer,
) : TokenStream {

    override fun isAtEnd(): Boolean = lexer.peek() == null

    override fun peak(): Token? = lexer.peek()

    override fun next(): StreamResult? {
        val lexerResult = lexer.next() ?: return null

        val nextStream = LexerTokenStream(lexerResult.lexer)

        return StreamResult(lexerResult.token, nextStream)
    }
}
