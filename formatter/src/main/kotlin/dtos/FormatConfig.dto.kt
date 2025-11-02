package dtos

data class FormatConfigDTO(
    val spaceBeforeColon: Boolean = false,
    val spaceAfterColon: Boolean = true,
    val spaceAroundAssignment: Boolean = true,
    val blankLinesAfterPrintln: Int = 1,
    val indentSize: Int = 4,
    val ifBraceOnSameLine: Boolean = true,
    val enforceSingleSpace: Boolean = true,
    val spaceAroundOperators: Boolean = true,
)
