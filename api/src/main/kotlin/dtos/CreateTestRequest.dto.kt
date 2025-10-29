package api.dtos

data class CreateTestRequest(
    val mainCodeId: Long,
    val inputs: String,
    val outputs: String,
    val expectedOutcomeIsSuccess: Boolean,
)
