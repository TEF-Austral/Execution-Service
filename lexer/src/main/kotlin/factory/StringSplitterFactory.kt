package factory

import string.streaming.StreamingSplitter
import java.io.Reader
import string.splitter.Splitter

object StringSplitterFactory : SplitterFactory {

    override fun createDefaultsSplitter(): Splitter {
        val specials = SpecialCharsFactory.createSpecialChars()
        return Splitter(specials)
    }

    override fun createStreamingSplitter(
        reader: Reader,
        size: Int,
    ): StreamingSplitter {
        val specialChars = SpecialCharsFactory.createSpecialChars()
        val tokenExtractor = TokenExtractorFactory.createTokenExtractor(specialChars)
        return StreamingSplitter(reader, tokenExtractor, bufferSize = size)
    }

    override fun createCustomSplitter(specialChars: List<Char>): Splitter = Splitter(specialChars)
}
