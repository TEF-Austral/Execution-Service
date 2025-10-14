package node

import coordinates.Coordinates
import coordinates.UnassignedPosition

class EmptyStatement(
    private val coordinates: Coordinates = UnassignedPosition(),
) : Statement {
    override fun toString(): String = "EmptyStatement"

    override fun getCoordinates(): Coordinates = coordinates
}
