package converter.specific

import coordinates.Coordinates
import PrintScriptToken
import Token
import type.CommonTypes

object NumberLiteralToToken : StringToTokenConverter {
    override fun canHandle(input: String): Boolean = input.toDoubleOrNull() != null

    override fun convert(
        input: String,
        position: Coordinates,
    ): Token =
        PrintScriptToken(type = CommonTypes.NUMBER_LITERAL, value = input, coordinates = position)
}
