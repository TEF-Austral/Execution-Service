package api.handlers

import component.AssetServiceClient
import consumers.handlers.ILintingRequestHandler
import org.springframework.stereotype.Service
import producers.LintingResultProducer
import services.AnalyzerService
import dtos.ValidationResultDTO
import requests.LintingRequestEvent
import result.LintingResultEvent
import result.ViolationDTO
import java.io.ByteArrayInputStream

@Service
class LintingRequestHandler(
    private val analyzerService: AnalyzerService,
    private val assetServiceClient: AssetServiceClient,
    private val resultProducer: LintingResultProducer,
) : ILintingRequestHandler {
    override fun handle(request: LintingRequestEvent) {
        println("🔍 [PrintScript] Processing linting request: ${request.requestId}")

        try {
            val content =
                assetServiceClient.getAsset(
                    request.bucketContainer,
                    request.bucketKey,
                )

            val inputStream = ByteArrayInputStream(content.toByteArray())
            val validation = analyzerService.analyze(inputStream, request.version, request.userId)

            val result =
                when (validation) {
                    is ValidationResultDTO.Valid -> {
                        LintingResultEvent(
                            requestId = request.requestId,
                            isValid = true,
                            violations = emptyList(),
                        )
                    }
                    is ValidationResultDTO.Invalid -> {
                        LintingResultEvent(
                            requestId = request.requestId,
                            isValid = false,
                            violations =
                                validation.violations.map {
                                    ViolationDTO(it.message, it.line, it.column)
                                },
                        )
                    }
                }

            resultProducer.emit(result)
            println("✅ [PrintScript] Linting completed: ${request.requestId}")
        } catch (e: Exception) {
            println("❌ [PrintScript] Linting failed: ${e.message}")
        }
    }
}
