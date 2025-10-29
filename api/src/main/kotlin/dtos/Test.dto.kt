package api.dtos

data class TestDTO(
    val mainCodeId: Long,
    val inputs: String,
    val outputs: String,
    val expectedOutcomeIsSuccess: Boolean,
)
