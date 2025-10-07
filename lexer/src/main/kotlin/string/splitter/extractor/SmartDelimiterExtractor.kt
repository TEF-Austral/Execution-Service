package string.splitter.extractor

class SmartDelimiterExtractor(
    private val specialChars: List<Char> = listOf(':', '='),
) : TokenExtractor {
    private val twoCharOps = setOf("!=", "<=", ">=", "==", "++", "--", "&&", "||")

    override fun extract(
        input: String,
        index: Int,
    ): Extraction {
        var cursor = index

        while (cursor < input.length && input[cursor].isWhitespace()) {
            cursor++
        }

        if (cursor >= input.length || !specialChars.contains(input[cursor])) {
            return Extraction.NoMatch
        }

        val delimiterStart = cursor
        val c = input[delimiterStart]
        val delimiter =
            if (delimiterStart + 1 < input.length) {
                val pair = "$c${input[delimiterStart + 1]}"
                if (pair in twoCharOps) pair else c.toString()
            } else {
                c.toString()
            }
        cursor += delimiter.length

        while (cursor < input.length && input[cursor].isWhitespace()) {
            cursor++
        }

        return Extraction.Token(input.substring(index, cursor))
    }
}
