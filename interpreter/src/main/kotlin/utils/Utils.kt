package utils
import type.CommonTypes

fun areTypesCompatible(
    declaredType: CommonTypes,
    valueType: CommonTypes,
): Boolean {
    if (declaredType == CommonTypes.NUMBER && valueType == CommonTypes.NUMBER_LITERAL) {
        return true
    }

    if (declaredType == CommonTypes.STRING && valueType == CommonTypes.STRING_LITERAL) {
        return true
    }

    if (declaredType == CommonTypes.BOOLEAN && valueType == CommonTypes.BOOLEAN_LITERAL) {
        return true
    }

    if (declaredType == CommonTypes.EMPTY || valueType == CommonTypes.EMPTY) {
        return true
    }

    return declaredType == valueType
}
