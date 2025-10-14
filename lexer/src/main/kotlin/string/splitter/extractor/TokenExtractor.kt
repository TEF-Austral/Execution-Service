package string.splitter.extractor

interface TokenExtractor {
    fun extract(
        input: String,
        index: Int,
    ): Extraction
}
