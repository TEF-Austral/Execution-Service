package dtos

data class ExecutionResponseDTO(
    val outputs: List<String>,
    val errors: List<String>,
    val success: Boolean,
)
