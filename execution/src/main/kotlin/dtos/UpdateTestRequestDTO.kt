package dtos

data class UpdateTestRequestDTO(
    val name: String?,
    val inputs: List<String>?,
    val expectedOutputs: List<String>?,
)
