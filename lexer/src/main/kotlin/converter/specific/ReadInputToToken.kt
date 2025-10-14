package converter.specific

import PrintScriptToken
import Token
import coordinates.Coordinates
import type.CommonTypes

object ReadInputToToken : StringToTokenConverter {
    override fun canHandle(input: String): Boolean = input == "readInput"

    override fun convert(
        input: String,
        position: Coordinates,
    ): Token =
        PrintScriptToken(type = CommonTypes.READ_INPUT, value = input, coordinates = position)
}
