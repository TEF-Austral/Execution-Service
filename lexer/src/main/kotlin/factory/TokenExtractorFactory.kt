package factory

import string.splitter.extractor.CommentExtractor
import string.splitter.extractor.NumberExtractor
import string.splitter.extractor.SmartDelimiterExtractor
import string.splitter.extractor.SpecialTokenExtractor
import string.splitter.extractor.StringLiteralExtractor
import string.splitter.extractor.TokenExtractor
import string.splitter.extractor.WhitespaceExtractor
import string.splitter.extractor.WordExtractor

object TokenExtractorFactory {
    fun createTokenExtractor(specialChars: List<Char>): List<TokenExtractor> =
        listOf(
            SmartDelimiterExtractor(),
            WhitespaceExtractor,
            CommentExtractor,
            StringLiteralExtractor,
            NumberExtractor(),
            SpecialTokenExtractor(specialChars),
            WordExtractor(specialChars),
        )
}
