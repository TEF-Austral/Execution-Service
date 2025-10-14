package node

import Token
import coordinates.Coordinates

class PrintStatement(
    private val expression: Expression,
    private val coordinates: Coordinates,
) : Statement {
    fun getExpression(): Expression = expression

    fun getExpressionToken(): Token? =
        if (expression is IdentifierExpression) {
            expression.getToken()
        } else {
            null
        }

    override fun getCoordinates(): Coordinates = coordinates

    override fun toString(): String = "PrintStatement($expression)"
}
