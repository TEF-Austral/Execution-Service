package converter.specific

import coordinates.Coordinates
import PrintScriptToken
import Token
import type.CommonTypes

object BooleanLiteralToToken : StringToTokenConverter {
    private val acceptedBooleanLiterals = setOf("true", "false")

    override fun canHandle(input: String): Boolean = input in acceptedBooleanLiterals

    override fun convert(
        input: String,
        position: Coordinates,
    ): Token =
        PrintScriptToken(
            type = CommonTypes.BOOLEAN_LITERAL,
            value = input,
            coordinates = position,
        )
}
