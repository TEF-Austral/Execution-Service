package node

import Token
import coordinates.Coordinates

class BinaryExpression(
    private val left: Expression,
    private val operator: Token,
    private val right: Expression,
    private val coordinates: Coordinates,
    // optional raw operator token that may include original spacing like " + " or "+ "
    private val operatorWithSpacing: Token? = null,
) : Expression {
    fun getLeft(): Expression = left

    fun getOperator(): String = operator.getValue()

    fun getRight(): Expression = right

    fun getOperatorTokenRaw(): Token? = operatorWithSpacing

    override fun getCoordinates(): Coordinates = coordinates

    override fun toString(): String = "BinaryExpression($left, ${operator.getValue()}, $right)"
}
