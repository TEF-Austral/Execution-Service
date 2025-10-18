package api.dtos

data class TestExecutionResult(
    val expectedOutcomeIsSuccess: Boolean,
    val output: String,
    val error: String? = null,
)
