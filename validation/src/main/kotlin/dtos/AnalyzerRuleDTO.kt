package dtos

data class AnalyzerRuleDTO(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val value: String? = null,
)
