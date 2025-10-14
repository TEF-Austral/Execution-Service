package string.splitter

import coordinates.Coordinates
import coordinates.Position
import string.splitter.extractor.CommentExtractor
import string.splitter.extractor.Extraction
import string.splitter.extractor.NumberExtractor
import string.splitter.extractor.SpecialTokenExtractor
import string.splitter.extractor.StringLiteralExtractor
import string.splitter.extractor.TokenExtractor
import string.splitter.extractor.WhitespaceExtractor
import string.splitter.extractor.WordExtractor

class Splitter(
    specialChars: List<Char>,
) {
    private val extractors: List<TokenExtractor> = createExtractors(specialChars)

    fun split(input: String): List<Pair<String, Coordinates>> {
        val context = SplitContext(input)

        while (context.hasMoreCharacters()) {
            val matched = tryExtractToken(context)
            if (!matched) {
                handleUnmatchedCharacter(context)
            }
        }

        return context.getTokens()
    }

    private fun createExtractors(specialChars: List<Char>): List<TokenExtractor> =
        listOf(
            SpecialTokenExtractor(specialChars),
            WhitespaceExtractor,
            CommentExtractor,
            StringLiteralExtractor,
            NumberExtractor(),
            SpecialTokenExtractor(specialChars),
            WordExtractor(specialChars),
        )

    private fun tryExtractToken(context: SplitContext): Boolean {
        for (extractor in extractors) {
            val extraction = extractor.extract(context.input, context.position)
            if (processExtraction(extraction, context)) {
                return true
            }
        }
        return false
    }

    private fun processExtraction(
        extraction: Extraction,
        context: SplitContext,
    ): Boolean =
        when (extraction) {
            is Extraction.NoMatch -> false
            is Extraction.Skip -> {
                context.skip(extraction.text)
                true
            }

            is Extraction.Token -> {
                context.addToken(extraction.value)
                true
            }
        }

    private fun handleUnmatchedCharacter(context: SplitContext) {
        context.skipSingleCharacter()
    }

    private class SplitContext(
        val input: String,
    ) {
        private val tokens = mutableListOf<Pair<String, Coordinates>>()
        var position = 0
            private set
        var line = 1
            private set
        var column = 1
            private set

        fun hasMoreCharacters(): Boolean = position < input.length

        fun getCurrentChar(): Char = input[position]

        fun addToken(value: String) {
            tokens.add(value to Position(line, column))
            advanceBy(value.length)
        }

        fun skip(text: String) {
            text.forEach { char -> updateCursor(char) }
            position += text.length
        }

        fun skipSingleCharacter() {
            updateCursor(getCurrentChar())
            position++
        }

        private fun advanceBy(length: Int) {
            column += length
            position += length
        }

        private fun updateCursor(char: Char) {
            when (char) {
                '\n' -> {
                    line++
                    column = 1
                }

                '\t' -> column += 4
                else -> column++
            }
        }

        fun getTokens(): List<Pair<String, Coordinates>> = tokens
    }
}
