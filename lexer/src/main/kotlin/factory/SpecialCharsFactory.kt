package factory

object SpecialCharsFactory {

    fun createSpecialChars(): List<Char> =
        listOf(
            '(',
            ')',
            '{',
            '}',
            '=',
            '<',
            '>',
            ';',
            ',',
            ':',
            '+',
            '-',
            '*',
            '/',
            '%',
            '&',
            '|',
            '!',
        )
}
