package helpers

import checkers.IdentifierStyle
import config.AnalyzerConfig
import java.io.InputStream
import kotlin.text.lowercase
import kotlin.text.toBoolean

object InputStreamToAnalyzerConfig {
    fun adapt(config: InputStream): AnalyzerConfig {
        val entries = convert(config)

        val identifierStyle = entries["identifier_format"]?.let { format ->
            when (format.lowercase()) {
                "snake case" -> IdentifierStyle.SNAKE_CASE
                "camel case" -> IdentifierStyle.CAMEL_CASE
                else -> IdentifierStyle.NO_STYLE
            }
        } ?: IdentifierStyle.NO_STYLE

        val restrictPrintlnArgs = entries["mandatory-variable-or-literal-in-println"]?.toBoolean()
            ?: false

        val restrictReadInputArgs = entries["mandatory-variable-or-literal-in-readInput"]?.toBoolean()
            ?: false

        return AnalyzerConfig(
            identifierStyle = identifierStyle,
            restrictPrintlnArgs = restrictPrintlnArgs,
            restrictReadInputArgs = restrictReadInputArgs
        )
    }

    private fun convert(configText: InputStream): Map<String, String> {
        val config = configText.bufferedReader().use { it.readText() }
        val configToJson =  config
            .trim()
            .removePrefix("{")
            .removeSuffix("}")
            .split(',')
            .map { it.trim().split(':', limit = 2).map(String::trim) }
            .filter { it.size == 2 }
            .associate { it[0].removeSurrounding("\"") to it[1].removeSurrounding("\"") }
        return configToJson
    }
}