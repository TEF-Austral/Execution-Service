package factory

import string.streaming.StreamingSplitter
import java.io.Reader
import string.splitter.Splitter

interface SplitterFactory {
    fun createDefaultsSplitter(): Splitter

    fun createStreamingSplitter(
        reader: Reader,
        size: Int = 65536,
    ): StreamingSplitter

    fun createCustomSplitter(specialChars: List<Char>): Splitter
}
