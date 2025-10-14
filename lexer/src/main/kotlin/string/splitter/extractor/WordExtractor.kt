package string.splitter.extractor

class WordExtractor(
    private val specialChars: List<Char>,
) : TokenExtractor {
    override fun extract(
        input: String,
        index: Int,
    ): Extraction {
        val c = input[index]
        if (c.isWhitespace() || specialChars.contains(c) || c == '"' || c == '\'') {
            return Extraction.NoMatch
        }

        var end = index
        while (
            end < input.length &&
            !input[end].isWhitespace() &&
            !specialChars.contains(input[end])
        ) {
            end++
        }
        return Extraction.Token(input.substring(index, end))
    }
}
