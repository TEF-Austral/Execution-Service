package dtos

data class WebSocketMessage(
    val type: WebSocketMessageType,
    val value: String? = null,
    val prompt: String? = null,
    val bucketContainer: String? = null,
    val bucketKey: String? = null,
    val version: String? = null,
)

enum class WebSocketMessageType {
    Output,

    InputRequest,

    ExecutionFinished,

    Error,

    InputResponse,

    InitExecution,
}
