package string.splitter.extractor

sealed class Extraction {
    object NoMatch : Extraction()

    data class Skip(
        val text: String,
    ) : Extraction()

    data class Token(
        val value: String,
    ) : Extraction()
}
