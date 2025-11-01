package dtos

data class InteractiveExecutionRequestDTO(
    val container: String,
    val key: String,
    val version: String,
    val inputs: Map<String, String>,
)
