package node

import Token
import coordinates.Coordinates
import type.CommonTypes

class LiteralExpression(
    private val token: Token,
    private val coordinates: Coordinates,
) : Expression {
    fun getValue(): String = token.getValue()

    fun getType(): CommonTypes = token.getType()

    override fun getCoordinates(): Coordinates = coordinates

    override fun toString(): String = getValue()
}
