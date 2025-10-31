package dtos

sealed class ValidationResultDTO {
    object Valid : ValidationResultDTO()
    data class Invalid(val violations: List<LintViolationDTO>) : ValidationResultDTO()
}