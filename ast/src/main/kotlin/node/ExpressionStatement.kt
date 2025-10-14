package node

import coordinates.Coordinates

class ExpressionStatement(
    private val expression: Expression,
    private val coordinates: Coordinates,
) : Statement {
    fun getExpression(): Expression = expression

    override fun getCoordinates(): Coordinates = coordinates
}
