package services

import dtos.FormatConfigDTO
import factories.LexerTokenStreamFactory
import formatter.factory.DefaultFormatterFactory.createFormatter
import mappers.FormatterConfigMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import transformer.StringToPrintScriptVersion
import java.io.InputStream
import java.io.StringWriter

@Service
class FormatterService(
    private val tokenStreamFactory: LexerTokenStreamFactory,
    private val configMapper: FormatterConfigMapper,
) {
    private val log = LoggerFactory.getLogger(FormatterService::class.java)

    fun format(
        src: InputStream,
        version: String,
        configDTO: FormatConfigDTO,
    ): String {
        log.info("Formatting code with version $version")

        val tokens = tokenStreamFactory.createTokenStream(src, version)
        val formatter = createFormatter(getAdaptedVersion(version))
        val config = configMapper.dtoToFormatConfig(configDTO)

        println("Analyzer Config: $config")

        val result = formatToString(formatter, tokens, config)
        log.warn("Code formatted successfully")
        return result
    }

    private fun getAdaptedVersion(version: String) = StringToPrintScriptVersion().transform(version)

    private fun formatToString(
        formatter: Any,
        tokens: Any,
        config: Any,
    ): String {
        val writer = StringWriter()
        (formatter as formatter.Formatter).formatToWriter(
            tokens as stream.token.LexerTokenStream,
            config as formatter.config.FormatConfig,
            writer,
        )
        return writer.toString()
    }
}
