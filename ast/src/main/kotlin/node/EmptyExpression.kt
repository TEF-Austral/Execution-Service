package node

import coordinates.Coordinates
import coordinates.UnassignedPosition

class EmptyExpression(
    private val coordinates: Coordinates = UnassignedPosition(),
) : Expression {
    override fun toString(): String = "EmptyExpression"

    override fun getCoordinates(): Coordinates = coordinates
}
