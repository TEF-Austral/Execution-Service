package handlers

import component.AssetServiceClient
import result.FormattingResultEvent
import org.springframework.stereotype.Service
import producers.AsyncTaskResultProducer
import requests.FormattingRequestEvent
import services.FormatterConfigService
import services.FormatterService
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@Service
class FormattingRequestHandler(
    private val assetServiceClient: AssetServiceClient,
    private val formatterService: FormatterService,
    private val formatterConfigService: FormatterConfigService,
    private val resultProducer: AsyncTaskResultProducer,
) {

    fun handleFormattingRequest(request: FormattingRequestEvent) {
        println("🔨 [PrintScript Service] Processing formatting request: ${request.requestId}")

        try {
            val content = assetServiceClient.getAsset(request.bucketContainer, request.bucketKey)
            val rules = formatterConfigService.getConfig(request.userId)
            val config = formatterConfigService.rulesToConfigDTO(rules)

            val inputStream = ByteArrayInputStream(content.toByteArray(StandardCharsets.UTF_8))
            val formattedContent = formatterService.format(inputStream, request.version, config)

            resultProducer.publishFormattingResult(
                FormattingResultEvent(
                    requestId = request.requestId,
                    snippetId = request.snippetId,
                    success = true,
                    formattedContent = formattedContent,
                    error = null,
                ),
            )

            println(
                "✅ [PrintScript Service] Formatting completed for request: ${request.requestId}",
            )
        } catch (e: Exception) {
            println(
                "❌ [PrintScript Service] Formatting failed for request: ${request.requestId}: ${e.message}",
            )

            resultProducer.publishFormattingResult(
                FormattingResultEvent(
                    requestId = request.requestId,
                    snippetId = request.snippetId,
                    success = false,
                    formattedContent = null,
                    error = e.message,
                ),
            )
        }
    }
}
