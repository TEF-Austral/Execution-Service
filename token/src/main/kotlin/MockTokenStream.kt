import result.StreamResult

class MockTokenStream(
    private val tokens: List<Token>,
    private val current: Int = 0,
) : TokenStream {
    override fun peak(): Token? = if (current < tokens.size) tokens[current] else null

    override fun next(): StreamResult? {
        if (current >= tokens.size) return null
        return StreamResult(tokens[current], MockTokenStream(tokens, current + 1))
    }

    override fun isAtEnd(): Boolean = current >= tokens.size
}
