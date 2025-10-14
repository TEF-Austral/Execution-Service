package string.streaming

import coordinates.Position

data class SplitterState(
    val buffer: String = "",
    val cursor: Cursor = Cursor(Position(1, 1)),
    val isStreamFinished: Boolean = false,
    val logicalUnit: List<Char> = listOf(';', '\n'),
) {
    val hasData: Boolean get() = buffer.isNotEmpty()

    fun hasCompleteLogicalUnit(): Boolean = isStreamFinished || buffer.any { it in logicalUnit }

    fun consume(length: Int): SplitterState {
        require(length <= buffer.length) {
            "Cannot consume more text than available in buffer: requested $length, available ${buffer.length}"
        }
        val consumedText = buffer.take(length)
        val newBuffer = buffer.substring(length)
        val newPosition = cursor.advance(consumedText)
        return this.copy(buffer = newBuffer, cursor = newPosition)
    }
}
