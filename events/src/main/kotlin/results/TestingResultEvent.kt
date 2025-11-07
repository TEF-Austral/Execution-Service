package results

data class TestingResultEvent(
    val requestId: String,
    val testId: Long,
    val snippetId: Long,
    val passed: Boolean,
    val outputs: List<String>,
    val expectedOutputs: List<String>,
    val errors: List<String>,
)
