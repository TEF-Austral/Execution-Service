package helpers

import builder.DefaultNodeBuilder
import factory.DefaultLexerFactory
import factory.StringSplitterFactory
import factory.StringToTokenConverterFactory
import parser.ParserInterface
import parser.factory.DefaultParserFactory
import stream.token.LexerTokenStream
import transformer.StringToPrintScriptVersion
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object ParserFactory {
    fun parse(
        src: InputStream,
        version: String,
    ): ParserInterface {
        val reader = BufferedReader(InputStreamReader(src, StandardCharsets.UTF_8))
        val lexerFactory = DefaultLexerFactory(StringSplitterFactory, StringToTokenConverterFactory)
        val adaptedVersion =
            when (version) {
                "1.1.0" -> StringToPrintScriptVersion().transform("1.1")
                "1.0.0" -> StringToPrintScriptVersion().transform("1.0")
                else -> StringToPrintScriptVersion().transform(version)
            }

        val lexer = lexerFactory.createLexerWithVersion(adaptedVersion, reader)
        val tokens = LexerTokenStream(lexer)
        return DefaultParserFactory.createWithVersion(adaptedVersion, DefaultNodeBuilder(), tokens)
    }
}
