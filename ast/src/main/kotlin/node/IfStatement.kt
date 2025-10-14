package node

import coordinates.Coordinates

class IfStatement(
    private val condition: Expression,
    private val consequence: Statement,
    private val alternative: Statement? = null,
    private val coordinates: Coordinates,
) : Statement {
    fun getCondition(): Expression = condition

    fun getConsequence(): Statement = consequence

    fun hasAlternative(): Boolean = alternative != null

    fun getAlternative(): Statement? = alternative

    override fun getCoordinates(): Coordinates = coordinates
}
