package api.controllers

import api.dtos.AnalyzeResponseDTO
import dtos.ValidationResultDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import services.AnalyzerService

@RestController
@RequestMapping("/analyze")
class AnalyzerController(
    private val analyzerService: AnalyzerService
) {

    @PostMapping
    fun analyzeCode(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("version") version: String,
        @RequestParam(value = "userId", required = false) userId: String?
    ): ResponseEntity<AnalyzeResponseDTO> {
        val inputStream = file.inputStream

        val result = analyzerService.validate(inputStream, version, userId)

        return when (result) {
            is ValidationResultDTO.Valid -> ResponseEntity.ok(
                AnalyzeResponseDTO(
                    isValid = true,
                    violations = emptyList()
                )
            )
            is ValidationResultDTO.Invalid -> ResponseEntity.ok(
                AnalyzeResponseDTO(
                    isValid = false,
                    violations = result.violations
                )
            )
        }
    }
}