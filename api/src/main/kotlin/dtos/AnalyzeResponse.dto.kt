package api.dtos

import dtos.LintViolationDTO

data class AnalyzeResponseDTO(
    val isValid: Boolean,
    val violations: List<LintViolationDTO>,
)
