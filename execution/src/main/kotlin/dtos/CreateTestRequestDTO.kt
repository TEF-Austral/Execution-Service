package dtos

data class CreateTestRequestDTO(
    val snippetId: Long,
    val name: String,
    val inputs: List<String>?,
    val expectedOutputs: List<String>?,
)
