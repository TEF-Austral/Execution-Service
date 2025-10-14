package variable

import type.CommonTypes

class Variable(
    private val type: CommonTypes,
    private val value: Any?,
) {
    fun getType(): CommonTypes = type

    fun getValue(): Any? = value
}
