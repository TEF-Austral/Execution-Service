package result

data class FormattingResultEvent(
    val requestId: String,
    val snippetId: Long,
    val success: Boolean,
    val formattedContent: String?,
    val error: String?,
)
