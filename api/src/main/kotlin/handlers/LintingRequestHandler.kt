package handlers

import component.AssetServiceClient
import consumers.handlers.ILintingRequestHandler
import dtos.ValidationResultDTO
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import producers.LintingResultProducer
import requests.LintingRequestEvent
import services.LanguagesAnalyzerService
import java.io.ByteArrayInputStream
import dtos.responses.LintingResultEvent
import dtos.responses.ViolationDTO

@Service
class LintingRequestHandler(
    private val languagesAnalyzerService: LanguagesAnalyzerService,
    private val assetServiceClient: AssetServiceClient,
    private val resultProducer: LintingResultProducer,
) : ILintingRequestHandler {

    private val log = LoggerFactory.getLogger(LintingRequestHandler::class.java)

    override fun handle(request: LintingRequestEvent) {
        try {
            val content = assetServiceClient.getAsset(request.bucketContainer, request.bucketKey)
            val inputStream = ByteArrayInputStream(content.toByteArray())
            val language = Language.valueOf(request.languageId)
            val validation =
                languagesAnalyzerService.analyze(
                    inputStream,
                    request.version,
                    request.userId,
                    language,
                )
            val result = createResultEvent(validation, request)
            resultProducer.emit(result)
        } catch (e: Exception) {
            log.error("Linting failed: ${e.message}", e)
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
                            ViolationDTO(
                                it.message,
                                it.line,
                                it.column,
                            )
                        },
                    snippetId = request.snippetId,
                )
            }
        }
}
