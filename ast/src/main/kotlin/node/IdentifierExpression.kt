package node

import Token
import coordinates.Coordinates

class IdentifierExpression(
    private val token: Token,
    private val coordinates: Coordinates,
) : Expression {
    fun getValue(): String = token.getValue()

    fun getToken(): Token = token

    override fun getCoordinates(): Coordinates = coordinates

    override fun toString(): String = getValue()
}
