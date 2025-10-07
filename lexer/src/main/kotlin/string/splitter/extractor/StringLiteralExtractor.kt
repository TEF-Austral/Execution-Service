package string.splitter.extractor

object StringLiteralExtractor : TokenExtractor {
    override fun extract(
        input: String,
        index: Int,
    ): Extraction {
        val quote = input[index]
        if (quote != '"' && quote != '\'') return Extraction.NoMatch

        val sb = StringBuilder().append(quote)
        var i = index + 1
        while (i < input.length) {
            sb.append(input[i])
            if (input[i] == quote) break
            i++
        }
        return Extraction.Token(sb.toString())
    }
}
