package dtos

data class UpdateAnalyzerConfigRequestDTO(
    val rules: List<AnalyzerRuleDTO>,
)
