package handlers

import component.AssetServiceClient
import consumers.handlers.ILintingRequestHandler
import org.springframework.stereotype.Service
import producers.LintingResultProducer
import services.AnalyzerService
import dtos.ValidationResultDTO
import requests.LintingRequestEvent
import results.LintingResultEvent
import results.ViolationDTO
import java.io.ByteArrayInputStream

@Service
class LintingRequestHandler(
    private val analyzerService: AnalyzerService,
    private val assetServiceClient: AssetServiceClient,
    private val resultProducer: LintingResultProducer,
) : ILintingRequestHandler {
    override fun handle(request: LintingRequestEvent) {
        try {
            val content = assetServiceClient.getAsset(request.bucketContainer, request.bucketKey)

            val inputStream = ByteArrayInputStream(content.toByteArray())
            val validation = analyzerService.analyze(inputStream, request.version, request.userId)

            val result = createResultEvent(validation, request)

            resultProducer.emit(result)
        } catch (e: Exception) {
            println("[PrintScript] Linting failed: ${e.message}")
            val result = createResultEvent(ValidationResultDTO.Invalid(emptyList()), request)
            resultProducer.emit(result)
        }
    }

    private fun createResultEvent(
        validation: ValidationResultDTO,
        request: LintingRequestEvent,
    ): LintingResultEvent =
        when (validation) {
            is ValidationResultDTO.Valid -> {
                LintingResultEvent(
                    requestId = request.requestId,
                    isValid = true,
                    violations = emptyList(),
                    snippetId = request.snippetId,
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
                    snippetId = request.snippetId,
                )
            }
        }
}
