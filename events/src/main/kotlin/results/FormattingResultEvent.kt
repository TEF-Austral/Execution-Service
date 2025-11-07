package results

data class FormattingResultEvent(
    val requestId: String,
    val success: Boolean,
    val formattedContent: String?,
    val error: String?,
)
