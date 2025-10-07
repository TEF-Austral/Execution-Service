package converter.specific

import coordinates.Coordinates
import PrintScriptToken
import Token
import type.CommonTypes

object ComparisonToToken : StringToTokenConverter {
    override fun canHandle(input: String): Boolean =
        input in listOf("==", "!=", "<", ">", "<=", ">=")

    override fun convert(
        input: String,
        position: Coordinates,
    ): Token =
        PrintScriptToken(type = CommonTypes.COMPARISON, value = input, coordinates = position)
}
