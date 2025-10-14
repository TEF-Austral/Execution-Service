package converter.specific

import coordinates.Coordinates
import PrintScriptToken
import Token
import type.CommonTypes

object PrintToToken : StringToTokenConverter {
    override fun canHandle(input: String): Boolean = input == "println"

    override fun convert(
        input: String,
        position: Coordinates,
    ): Token = PrintScriptToken(type = CommonTypes.PRINT, value = input, coordinates = position)
}
