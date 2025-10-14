package converter.specific

import coordinates.Coordinates
import PrintScriptToken
import Token
import type.CommonTypes

object ConstDeclarationToToken : StringToTokenConverter {
    override fun canHandle(input: String): Boolean = input == "const"

    override fun convert(
        input: String,
        position: Coordinates,
    ): Token = PrintScriptToken(type = CommonTypes.CONST, value = input, coordinates = position)
}
