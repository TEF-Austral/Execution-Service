package handlers

import component.AssetServiceClient
import dtos.ValidationResultDTO
import requests.LintingRequestEvent
import result.LintingResultEvent
import result.ViolationDTO
import org.springframework.stereotype.Service
import producers.AsyncTaskResultProducer
import services.AnalyzerService
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@Service
class LintingRequestHandler(
    private val assetServiceClient: AssetServiceClient,
    private val analyzerService: AnalyzerService,
    private val resultProducer: AsyncTaskResultProducer,
) {

    fun handleLintingRequest(request: LintingRequestEvent) {
        println("🔨 [PrintScript Service] Processing linting request: ${request.requestId}")

        try {
            val content = assetServiceClient.getAsset(request.bucketContainer, request.bucketKey)
            val inputStream = ByteArrayInputStream(content.toByteArray(StandardCharsets.UTF_8))

            val result = analyzerService.validate(inputStream, request.version, request.userId)

            val violations =
                when (result) {
                    is ValidationResultDTO.Valid -> emptyList()
                    is ValidationResultDTO.Invalid ->
                        result.violations.map {
                            ViolationDTO(
                                message = it.message,
                                line = it.line,
                                column = it.column,
                            )
                        }
                }

            resultProducer.publishLintingResult(
                LintingResultEvent(
                    requestId = request.requestId,
                    snippetId = request.snippetId,
                    isValid = violations.isEmpty(),
                    violations = violations,
                ),
            )

            println("✅ [PrintScript Service] Linting completed for request: ${request.requestId}")
        } catch (e: Exception) {
            println(
                "❌ [PrintScript Service] Linting failed for request: ${request.requestId}: ${e.message}",
            )

            resultProducer.publishLintingResult(
                LintingResultEvent(
                    requestId = request.requestId,
                    snippetId = request.snippetId,
                    isValid = false,
                    violations =
                        listOf(
                            ViolationDTO(
                                message = "Linting failed: ${e.message}",
                                line = 0,
                                column = 0,
                            ),
                        ),
                ),
            )
        }
    }
}
