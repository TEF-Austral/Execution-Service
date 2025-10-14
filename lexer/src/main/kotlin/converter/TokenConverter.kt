package converter

import coordinates.Coordinates
import Token

interface TokenConverter {
    fun convert(
        input: String,
        position: Coordinates,
    ): Token
}
