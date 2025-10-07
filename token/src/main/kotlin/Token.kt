import type.CommonTypes
import coordinates.Coordinates

sealed interface Token {
    fun getType(): CommonTypes

    fun getValue(): String

    fun getCoordinates(): Coordinates
}
