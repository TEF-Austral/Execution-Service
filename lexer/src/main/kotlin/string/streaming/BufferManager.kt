package string.streaming

import java.io.Reader

class BufferManager(
    private val reader: Reader,
    private val bufferSize: Int = 65536,
) {
    fun ensureSufficientBuffer(currentState: SplitterState): SplitterState {
        if (shouldReturnState(currentState)) {
            return currentState
        }

        val charBuffer = CharArray(bufferSize)
        val charsRead =
            try {
                reader.read(charBuffer)
            } catch (e: Exception) {
                -1
            }

        return if (charsRead > 0) {
            val newBuffer = currentState.buffer + String(charBuffer, 0, charsRead)
            currentState.copy(buffer = newBuffer)
        } else {
            currentState.copy(isStreamFinished = true)
        }
    }

    private fun shouldReturnState(currentState: SplitterState): Boolean =
        currentState.isStreamFinished || currentState.hasCompleteLogicalUnit()
}
