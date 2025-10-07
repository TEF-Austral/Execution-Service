// kotlin
package node

import Token
import coordinates.Coordinates

class AssignmentStatement(
    private val identifier: Token,
    private val value: Expression,
    private val coordinates: Coordinates,
    private val assignmentOperator: Token? = null,
) : Statement {
    fun getIdentifier(): String = identifier.getValue()

    fun getValue(): Expression = value

    fun getIdentifierToken(): Token = identifier

    override fun getCoordinates(): Coordinates = coordinates
}
