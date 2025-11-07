package api.handlers

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
        println("🔨 [PrintScript] Processing formatting request: ${request.requestId}")

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

            val result =
                FormattingResultEvent(
                    requestId = request.requestId,
                    success = true,
                    formattedContent = formatted,
                    error = null,
                    snippetId = request.snippetId,
                )

            resultProducer.emit(result)
            println("✅ [PrintScript] Formatting completed: ${request.requestId}")
        } catch (e: Exception) {
            println("❌ [PrintScript] Formatting failed: ${e.message}")

            val result =
                FormattingResultEvent(
                    requestId = request.requestId,
                    success = false,
                    error = e.message,
                    formattedContent = null,
                    snippetId = request.snippetId,
                )

            resultProducer.emit(result)
        }
    }
}
