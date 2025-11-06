package controllers

import component.AssetServiceClient
import security.AuthenticatedUserProvider
import dtos.AnalyzeResponseDTO
import dtos.ValidationResultDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
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
    private val authenticatedUserProvider: AuthenticatedUserProvider,
) {

    @GetMapping
    fun analyzeCode(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
        @RequestParam("userId", required = false) userId: String?,
    ): ResponseEntity<AnalyzeResponseDTO> {
        val effectiveUserId = userId ?: authenticatedUserProvider.getCurrentUserId()

        val assetContent = assetServiceClient.getAsset(container, key)
        val inputStream = ByteArrayInputStream(assetContent.toByteArray(StandardCharsets.UTF_8))

        val result = analyzerService.validate(inputStream, version, effectiveUserId)

        return when (result) {
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
    }

    @GetMapping("/compile")
    fun compileCode(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
    ): ResponseEntity<AnalyzeResponseDTO> {
        val assetContent = assetServiceClient.getAsset(container, key)
        val inputStream = ByteArrayInputStream(assetContent.toByteArray(StandardCharsets.UTF_8))

        val result = analyzerService.compile(inputStream, version)

        return when (result) {
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
    }
}
