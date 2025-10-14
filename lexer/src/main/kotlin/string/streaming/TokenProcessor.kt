package string.streaming

import string.splitter.extractor.Extraction
import string.splitter.extractor.TokenExtractor

class TokenProcessor(
    private val extractors: List<TokenExtractor>,
) {

    private fun handleExtraction(
        extraction: Extraction,
        state: SplitterState,
    ): ProcessingResult? =
        when (extraction) {
            is Extraction.Token -> {
                val newState = state.consume(extraction.value.length)
                Success(extraction.value, newState)
            }
            is Extraction.Skip -> {
                Skip(state.consume(extraction.text.length))
            }
            is Extraction.NoMatch -> null
        }

    fun process(state: SplitterState): ProcessingResult? {
        if (!state.hasData) return null

        for (extractor in extractors) {
            val extraction = extractor.extract(state.buffer, 0)
            val result = handleExtraction(extraction, state)
            if (result != null) return result
        }

        return Retry(state.consume(1))
    }
}
