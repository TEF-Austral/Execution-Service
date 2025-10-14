import converter.TokenConverter
import string.streaming.StreamingSplitter

class DefaultLexer(
    private val tokenConverter: TokenConverter,
    splitter: StreamingSplitter,
) : Lexer {

    private val splitterResult = splitter.next()

    override fun next(): result.LexerResult? =
        if (splitterResult != null) {
            val token = tokenConverter.convert(splitterResult.string, splitterResult.coordinates)
            val nextLexer = DefaultLexer(tokenConverter, splitterResult.splitter)
            result.LexerResult(token, nextLexer)
        } else {
            null
        }

    override fun peek(): Token? =
        if (splitterResult != null) {
            tokenConverter.convert(splitterResult.string, splitterResult.coordinates)
        } else {
            null
        }
}
