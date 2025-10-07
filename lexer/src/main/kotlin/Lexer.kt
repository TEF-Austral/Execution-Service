import result.LexerResult

interface Lexer {

    fun next(): LexerResult?

    fun peek(): Token?
}
