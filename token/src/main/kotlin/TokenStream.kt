import result.StreamResult

interface TokenStream {
    fun peak(): Token?

    fun next(): StreamResult?

    fun isAtEnd(): Boolean
}
