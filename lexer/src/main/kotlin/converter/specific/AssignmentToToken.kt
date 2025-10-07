package converter.specific

import coordinates.Coordinates
import PrintScriptToken
import Token
import type.CommonTypes

object AssignmentToToken : StringToTokenConverter {
    override fun canHandle(input: String): Boolean = input.replace(" ", "") == "="

    override fun convert(
        input: String,
        position: Coordinates,
    ): Token =
        PrintScriptToken(type = CommonTypes.ASSIGNMENT, value = input, coordinates = position)
}
