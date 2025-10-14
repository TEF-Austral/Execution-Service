package converter

import coordinates.Coordinates
import PrintScriptToken
import Token
import type.CommonTypes
import converter.specific.StringToTokenConverter

data class TokenConverterRegistry(
    val list: List<StringToTokenConverter>,
) : TokenConverter {
    override fun convert(
        input: String,
        position: Coordinates,
    ): Token {
        for (converter in list) {
            if (converter.canHandle(input)) {
                return converter.convert(input, position)
            }
        }
        return PrintScriptToken(
            type = CommonTypes.IDENTIFIER,
            value = input,
            coordinates = position,
        )
    }
}
