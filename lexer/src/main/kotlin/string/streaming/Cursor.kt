package string.streaming

import coordinates.Coordinates
import coordinates.Position
import kotlin.text.iterator

data class Cursor(
    val position: Coordinates,
) {

    fun advance(text: String): Cursor {
        var currentPosition = this.position

        for (char in text) {
            currentPosition = advancePosition(currentPosition, char)
        }

        return Cursor(currentPosition)
    }

    private fun advancePosition(
        pos: Coordinates,
        char: Char,
    ): Coordinates =
        when (char) {
            '\n' ->
                Position(
                    row = pos.getRow() + 1,
                    column = 1,
                )
            '\r' -> pos
            else ->
                Position(
                    row = pos.getRow(),
                    column = pos.getColumn() + 1,
                )
        }
}
