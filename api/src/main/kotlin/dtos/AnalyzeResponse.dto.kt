package dtos

data class AnalyzeResponseDTO(
    val isValid: Boolean,
    val violations: List<LintViolationDTO>,
)
