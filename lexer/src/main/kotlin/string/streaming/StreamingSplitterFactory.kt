package string.streaming

import string.splitter.extractor.TokenExtractor
import java.io.Reader

object StreamingSplitterFactory {

    fun create(
        reader: Reader,
        extractors: List<TokenExtractor>,
        bufferSize: Int = 65536,
        logicalUnit: List<Char> = listOf(';', '\n'),
    ): StreamingSplitter = StreamingSplitter(reader, extractors, bufferSize, logicalUnit)
}
