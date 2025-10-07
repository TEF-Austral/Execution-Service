package converter.specific

import coordinates.Coordinates
import PrintScriptToken
import Token
import type.CommonTypes

object ConditionalToToken : StringToTokenConverter {
    override fun canHandle(input: String): Boolean = input in listOf("if", "else")

    override fun convert(
        input: String,
        position: Coordinates,
    ): Token =
        PrintScriptToken(type = CommonTypes.CONDITIONALS, value = input, coordinates = position)
}
