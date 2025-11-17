package handlers

import Language
import component.AssetServiceClient
import consumers.handlers.IFormattingRequestHandler
import org.slf4j.LoggerFactory
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

    private val log = LoggerFactory.getLogger(FormattingRequestHandler::class.java)

    override fun handle(request: FormattingRequestEvent) {
        try {
            val content = assetServiceClient.getAsset(request.bucketContainer, request.bucketKey)
            val rules = formatterConfigService.getConfig(request.userId)
            val config = formatterConfigService.rulesToConfigDTO(rules)
            val inputStream = ByteArrayInputStream(content.toByteArray())
            val language = Language.valueOf(request.languageId)
            val formatted =
                languagesFormatterService.format(
                    inputStream,
                    request.version,
                    config,
                    language,
                )
            val result = createResultEvent(formatted, null, request)
            resultProducer.emit(result)
        } catch (e: Exception) {
            log.error("Formatting failed: ${e.message}", e)
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
