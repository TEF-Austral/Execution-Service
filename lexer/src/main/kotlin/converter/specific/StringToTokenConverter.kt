package converter.specific

import converter.TokenConverter

sealed interface StringToTokenConverter : TokenConverter {
    fun canHandle(input: String): Boolean
}
