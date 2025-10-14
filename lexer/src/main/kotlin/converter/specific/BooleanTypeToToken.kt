package converter.specific

import coordinates.Coordinates
import PrintScriptToken
import Token
import type.CommonTypes

object BooleanTypeToToken : StringToTokenConverter {

    override fun canHandle(input: String): Boolean = input == "boolean"

    override fun convert(
        input: String,
        position: Coordinates,
    ): Token =
        PrintScriptToken(
            type = CommonTypes.BOOLEAN,
            value = input,
            coordinates = position,
        )
}
