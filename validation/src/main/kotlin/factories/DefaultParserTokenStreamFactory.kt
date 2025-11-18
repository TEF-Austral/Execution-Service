package factories

import builder.DefaultNodeBuilder
import factory.DefaultLexerFactory
import factory.StringSplitterFactory
import factory.StringToTokenConverterFactory
import parser.ParserInterface
import parser.factory.DefaultParserFactory
import stream.token.LexerTokenStream
import transformer.StringToPrintScriptVersion
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Component
class DefaultParserTokenStreamFactory : ParserTokenStreamFactory {
    private val lexerFactory =
        DefaultLexerFactory(StringSplitterFactory, StringToTokenConverterFactory)
    private val versionTransformer = StringToPrintScriptVersion()

    override fun createParser(
        src: InputStream,
        version: String,
    ): ParserInterface {
        val reader = createReader(src)
        val adaptedVersion = versionTransformer.transform(version)
        val lexer = lexerFactory.createLexerWithVersion(adaptedVersion, reader)
        val tokens = LexerTokenStream(lexer)
        return DefaultParserFactory.createWithVersion(adaptedVersion, DefaultNodeBuilder(), tokens)
    }

    private fun createReader(src: InputStream): BufferedReader =
        BufferedReader(InputStreamReader(src, StandardCharsets.UTF_8))
}
