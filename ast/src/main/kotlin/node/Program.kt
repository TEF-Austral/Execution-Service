package node

import coordinates.Coordinates
import coordinates.UnassignedPosition

class Program(
    private val statements: List<Statement>,
    private val coordinates: Coordinates = UnassignedPosition(),
) : ASTNode {
    fun getStatements(): List<Statement> = statements

    override fun getCoordinates(): Coordinates = coordinates
}
