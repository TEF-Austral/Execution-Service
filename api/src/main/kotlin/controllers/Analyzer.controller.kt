package controllers

import component.AssetServiceClient
import dtos.AnalyzeResponseDTO
import dtos.ValidationResultDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import services.AnalyzerService
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/analyze")
class AnalyzerController(
    private val analyzerService: AnalyzerService,
    private val assetServiceClient: AssetServiceClient,
) {
    private val log = org.slf4j.LoggerFactory.getLogger(AnalyzerController::class.java)

    @GetMapping
    fun analyzeCode(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
        @RequestParam("userId") userId: String,
    ): ResponseEntity<AnalyzeResponseDTO> {
        log.info("GET /analyze - Analyzing code for user $userId, version $version")
        val assetContent = assetServiceClient.getAsset(container, key)
        val inputStream = ByteArrayInputStream(assetContent.toByteArray(StandardCharsets.UTF_8))
        val result = analyzerService.analyze(inputStream, version, userId)

        val response =
            when (result) {
                is ValidationResultDTO.Valid ->
                    ResponseEntity.ok(
                        AnalyzeResponseDTO(
                            isValid = true,
                            violations = emptyList(),
                        ),
                    )
                is ValidationResultDTO.Invalid ->
                    ResponseEntity.ok(
                        AnalyzeResponseDTO(
                            isValid = false,
                            violations = result.violations,
                        ),
                    )
            }
        log.warn(
            "GET /analyze - Analysis completed, isValid: ${result is ValidationResultDTO.Valid}",
        )
        return response
    }

    @PostMapping("/validate")
    fun validateContent(
        @RequestBody content: String,
        @RequestParam("version") version: String,
    ): ResponseEntity<AnalyzeResponseDTO> {
        log.info("POST /analyze/validate - Validating content, version $version")
        val inputStream = ByteArrayInputStream(content.toByteArray(StandardCharsets.UTF_8))
        val result = analyzerService.compile(inputStream, version)

        val response =
            when (result) {
                is ValidationResultDTO.Valid ->
                    ResponseEntity.ok(
                        AnalyzeResponseDTO(
                            isValid = true,
                            violations = emptyList(),
                        ),
                    )
                is ValidationResultDTO.Invalid ->
                    ResponseEntity.ok(
                        AnalyzeResponseDTO(
                            isValid = false,
                            violations = result.violations,
                        ),
                    )
            }
        log.warn(
            "POST /analyze/validate - Validation completed, isValid: ${result is ValidationResultDTO.Valid}",
        )
        return response
    }

    @GetMapping("/compile")
    fun compileCode(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
    ): ResponseEntity<AnalyzeResponseDTO> {
        log.info("GET /analyze/compile - Compiling code, version $version")
        val assetContent = assetServiceClient.getAsset(container, key)
        val inputStream = ByteArrayInputStream(assetContent.toByteArray(StandardCharsets.UTF_8))

        val result = analyzerService.compile(inputStream, version)

        val response =
            when (result) {
                is ValidationResultDTO.Valid ->
                    ResponseEntity.ok(
                        AnalyzeResponseDTO(
                            isValid = true,
                            violations = emptyList(),
                        ),
                    )
                is ValidationResultDTO.Invalid ->
                    ResponseEntity.ok(
                        AnalyzeResponseDTO(
                            isValid = false,
                            violations = result.violations,
                        ),
                    )
            }
        log.warn(
            "GET /analyze/compile - Compilation completed, isValid: ${result is ValidationResultDTO.Valid}",
        )
        return response
    }
}
