package services

import dtos.FormatConfigDTO
import factory.DefaultLexerFactory
import factory.StringSplitterFactory
import factory.StringToTokenConverterFactory
import formatter.config.FormatConfig
import formatter.factory.DefaultFormatterFactory.createFormatter
import stream.token.LexerTokenStream
import transformer.StringToPrintScriptVersion
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import org.springframework.stereotype.Service

@Service
class FormatterService {
    private val log = org.slf4j.LoggerFactory.getLogger(FormatterService::class.java)

    fun format(
        src: InputStream,
        version: String,
        configDTO: FormatConfigDTO,
    ): String {
        log.info("Formatting code with version $version")
        val reader = BufferedReader(InputStreamReader(src, StandardCharsets.UTF_8))
        val lexerFactory = DefaultLexerFactory(StringSplitterFactory, StringToTokenConverterFactory)
        val adaptedVersion = StringToPrintScriptVersion().transform(version)
        val lexer = lexerFactory.createLexerWithVersion(adaptedVersion, reader)
        val tokens = LexerTokenStream(lexer)
        val formatter = createFormatter(adaptedVersion)

        val config =
            FormatConfig(
                spaceBeforeColon = configDTO.spaceBeforeColon,
                spaceAfterColon = configDTO.spaceAfterColon,
                spaceAroundAssignment = configDTO.spaceAroundAssignment,
                blankLinesAfterPrintln = configDTO.blankLinesAfterPrintln,
                indentSize = configDTO.indentSize,
                ifBraceOnSameLine = configDTO.ifBraceOnSameLine,
                enforceSingleSpace = configDTO.enforceSingleSpace,
                spaceAroundOperators = configDTO.spaceAroundOperators,
            )

        println("Analyzer Config: $config")

        val writer = StringWriter()
        formatter.formatToWriter(tokens, config, writer)
        val result = writer.toString()
        log.warn("Code formatted successfully")
        return result
    }
}
