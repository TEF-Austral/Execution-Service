package string.splitter.extractor

class NumberExtractor : TokenExtractor {
    override fun extract(
        input: String,
        index: Int,
    ): Extraction {
        if (!input[index].isDigit()) return Extraction.NoMatch

        var i = index
        while (i < input.length && input[i].isDigit()) i++
        if (i < input.length && input[i] == '.') {
            i++
            while (i < input.length && input[i].isDigit()) i++
        }

        return Extraction.Token(input.substring(index, i))
    }
}
