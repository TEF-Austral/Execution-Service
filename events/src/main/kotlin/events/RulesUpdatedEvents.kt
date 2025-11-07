package events

data class AnalyzerRulesUpdatedEvent(
    val userId: String,
)

data class FormattingRulesUpdatedEvent(
    val userId: String,
)
