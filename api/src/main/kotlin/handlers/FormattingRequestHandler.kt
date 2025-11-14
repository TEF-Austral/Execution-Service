package handlers

import Language
import component.AssetServiceClient
import consumers.handlers.IFormattingRequestHandler
import org.springframework.stereotype.Service
import producers.FormattingResultProducer
import requests.FormattingRequestEvent
import services.FormatterConfigService
import services.LanguagesFormatterService
import java.io.ByteArrayInputStream
import dtos.responses.FormattingResultEvent

@Service
class FormattingRequestHandler(
    private val languagesFormatterService: LanguagesFormatterService,
    private val formatterConfigService: FormatterConfigService,
    private val assetServiceClient: AssetServiceClient,
    private val resultProducer: FormattingResultProducer,
) : IFormattingRequestHandler {

    override fun handle(request: FormattingRequestEvent) {
        try {
            val content = assetServiceClient.getAsset(request.bucketContainer, request.bucketKey)
            val rules = formatterConfigService.getConfig(request.userId)
            val config = formatterConfigService.rulesToConfigDTO(rules)
            val inputStream = ByteArrayInputStream(content.toByteArray())
            // val language = Language.valueOf(request.language)
            val formatted =
                languagesFormatterService.format(
                    inputStream,
                    request.version,
                    config,
                    Language.PRINTSCRIPT,
                )
            val result = createResultEvent(formatted, null, request)
            resultProducer.emit(result)
        } catch (e: Exception) {
            println("[Language] Formatting failed: ${e.message}")
            val result = createResultEvent(null, e.message, request)
            resultProducer.emit(result)
        }
    }

    private fun createResultEvent(
        formattedContent: String?,
        error: String?,
        request: FormattingRequestEvent,
    ): FormattingResultEvent =
        FormattingResultEvent(
            requestId = request.requestId,
            success = formattedContent != null,
            formattedContent = formattedContent,
            error = error,
            snippetId = request.snippetId,
        )
}
