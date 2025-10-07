package node

import coordinates.Coordinates

class ReadEnvExpression(
    private val envName: String,
    private val coordinates: Coordinates,
) : CoercibleExpression {
    override fun getCoordinates(): Coordinates = coordinates

    fun envName(): String = envName
}
