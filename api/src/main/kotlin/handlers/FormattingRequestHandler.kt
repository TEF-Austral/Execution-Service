package handlers

import component.AssetServiceClient
import consumers.handlers.IFormattingRequestHandler
import org.springframework.stereotype.Service
import producers.FormattingResultProducer
import requests.FormattingRequestEvent
import results.FormattingResultEvent
import services.FormatterConfigService
import services.FormatterService
import java.io.ByteArrayInputStream

@Service
class FormattingRequestHandler(
    private val formatterService: FormatterService,
    private val formatterConfigService: FormatterConfigService,
    private val assetServiceClient: AssetServiceClient,
    private val resultProducer: FormattingResultProducer,
) : IFormattingRequestHandler {

    override fun handle(request: FormattingRequestEvent) {
        try {
            val content =
                assetServiceClient.getAsset(
                    request.bucketContainer,
                    request.bucketKey,
                )

            val rules = formatterConfigService.getConfig(request.userId)
            val config = formatterConfigService.rulesToConfigDTO(rules)

            val inputStream = ByteArrayInputStream(content.toByteArray())
            val formatted = formatterService.format(inputStream, request.version, config)

            val result = createResultEvent(formatted, null, request)

            resultProducer.emit(result)
        } catch (e: Exception) {
            println("[PrintScript] Formatting failed: ${e.message}")
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
