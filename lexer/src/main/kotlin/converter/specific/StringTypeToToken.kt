package converter.specific

import coordinates.Coordinates
import PrintScriptToken
import Token
import type.CommonTypes

object StringTypeToToken : StringToTokenConverter {

    override fun canHandle(input: String): Boolean = input == "string"

    override fun convert(
        input: String,
        position: Coordinates,
    ): Token =
        PrintScriptToken(
            type = CommonTypes.STRING,
            value = input,
            coordinates = position,
        )
}
