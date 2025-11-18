package factories

import factory.DefaultLexerFactory
import factory.StringSplitterFactory
import factory.StringToTokenConverterFactory
import org.springframework.stereotype.Component
import stream.token.LexerTokenStream
import transformer.StringToPrintScriptVersion
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Component
class DefaultLexerTokenStreamFactory : LexerTokenStreamFactory {
    private val lexerFactory =
        DefaultLexerFactory(StringSplitterFactory, StringToTokenConverterFactory)
    private val versionTransformer = StringToPrintScriptVersion()

    override fun createTokenStream(
        src: InputStream,
        version: String,
    ): LexerTokenStream {
        val reader = createReader(src)
        val adaptedVersion = versionTransformer.transform(version)
        val lexer = lexerFactory.createLexerWithVersion(adaptedVersion, reader)
        return LexerTokenStream(lexer)
    }

    private fun createReader(src: InputStream): BufferedReader =
        BufferedReader(InputStreamReader(src, StandardCharsets.UTF_8))
}
