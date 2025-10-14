package string.streaming

sealed interface ProcessingResult

data class Success(
    val token: String,
    val newState: SplitterState,
) : ProcessingResult

data class Skip(
    val newState: SplitterState,
) : ProcessingResult

data class Retry(
    val newState: SplitterState,
) : ProcessingResult
