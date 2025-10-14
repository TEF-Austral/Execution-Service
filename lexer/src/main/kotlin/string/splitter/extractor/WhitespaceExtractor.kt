package string.splitter.extractor

object WhitespaceExtractor : TokenExtractor {
    override fun extract(
        input: String,
        index: Int,
    ): Extraction =
        when (val c = input[index]) {
            ' ' -> Extraction.Skip(" ")
            '\t' -> Extraction.Skip("\t")
            '\n' -> Extraction.Skip("\n")
            else -> Extraction.NoMatch
        }
}
