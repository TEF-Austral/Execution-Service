package converter.specific

import coordinates.Coordinates
import PrintScriptToken
import Token
import type.CommonTypes

object LogicalOperatorToken : StringToTokenConverter {
    override fun canHandle(input: String): Boolean = input in listOf("&&", "||")

    override fun convert(
        input: String,
        position: Coordinates,
    ): Token =
        PrintScriptToken(
            type = CommonTypes.LOGICAL_OPERATORS,
            value = input,
            coordinates = position,
        )
}
