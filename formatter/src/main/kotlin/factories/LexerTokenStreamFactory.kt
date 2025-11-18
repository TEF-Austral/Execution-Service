package factories

import stream.token.LexerTokenStream
import java.io.InputStream

interface LexerTokenStreamFactory {
    fun createTokenStream(
        src: InputStream,
        version: String,
    ): LexerTokenStream
}
