package string.splitter.extractor

object CommentExtractor : TokenExtractor {
    override fun extract(
        input: String,
        index: Int,
    ): Extraction {
        if (index + 1 >= input.length) return Extraction.NoMatch

        if (input[index] == '/' && input[index + 1] == '/') {
            var i = index + 2
            while (i < input.length && input[i] != '\n') i++
            return Extraction.Skip(input.substring(index, i))
        }

        if (input[index] == '/' && input[index + 1] == '*') {
            var i = index + 2
            while (i + 1 < input.length && !(input[i] == '*' && input[i + 1] == '/')) {
                i++
            }
            if (i + 1 < input.length) i += 2
            return Extraction.Skip(input.substring(index, i))
        }

        return Extraction.NoMatch
    }
}
