package string.splitter.extractor

class SpecialTokenExtractor(
    private val specialChars: List<Char>,
) : TokenExtractor {
    private val twoCharOps = setOf("!=", "<=", ">=", "==", "++", "--", "&&", "||")

    override fun extract(
        input: String,
        index: Int,
    ): Extraction {
        val c = input[index]
        if (!specialChars.contains(c)) return Extraction.NoMatch

        val token =
            if (index + 1 < input.length) {
                val pair = "$c${input[index + 1]}"
                if (pair in twoCharOps) pair else c.toString()
            } else {
                c.toString()
            }

        return Extraction.Token(token)
    }
}
