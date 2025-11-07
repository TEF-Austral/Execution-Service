package results

data class LintingResultEvent(
    val requestId: String,
    val isValid: Boolean,
    val violations: List<ViolationDTO>,
)

data class ViolationDTO(
    val message: String,
    val line: Int,
    val column: Int,
)
