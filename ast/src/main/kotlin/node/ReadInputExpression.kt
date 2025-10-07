package node

import coordinates.Coordinates

class ReadInputExpression(
    private val printValue: Expression,
    private val coordinates: Coordinates,
) : CoercibleExpression {
    override fun getCoordinates(): Coordinates = coordinates

    fun printValue(): Expression = printValue
}
