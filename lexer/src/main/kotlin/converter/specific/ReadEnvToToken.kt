package converter.specific

import PrintScriptToken
import Token
import coordinates.Coordinates
import type.CommonTypes

object ReadEnvToToken : StringToTokenConverter {
    override fun canHandle(input: String): Boolean = input == "readEnv"

    override fun convert(
        input: String,
        position: Coordinates,
    ): Token = PrintScriptToken(type = CommonTypes.READ_ENV, value = input, coordinates = position)
}
