package controllers

import component.AssetServiceClient
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
) {

    @GetMapping
    fun analyzeCode(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
        @RequestParam(value = "userId", required = false) userId: String?,
    ): ResponseEntity<AnalyzeResponseDTO> {
        val assetContent = assetServiceClient.getAsset(container, key)
        val inputStream = ByteArrayInputStream(assetContent.toByteArray(StandardCharsets.UTF_8))

        val result = analyzerService.validate(inputStream, version, userId)

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
