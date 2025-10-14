package factory

import DefaultLexer
import Lexer
import converter.specific.StringToTokenConverter
import java.io.Reader
import string.streaming.StreamingSplitter
import type.Version
import type.Version.VERSION_1_0
import type.Version.VERSION_1_1

data class DefaultLexerFactory(
    val splitterFactory: StringSplitterFactory,
    val converterFactory: ConverterFactory,
) : LexerFactory {

    override fun createVersionOne(
        reader: Reader,
        size: Int,
        splitter: StreamingSplitter,
    ): Lexer {
        val converterList = converterFactory.createVersionOne()
        return DefaultLexer(converterList, splitter)
    }

    override fun createVersionOnePointOne(
        reader: Reader,
        size: Int,
        splitter: StreamingSplitter,
    ): Lexer {
        val converterList = converterFactory.createVersionOnePointOne()
        return DefaultLexer(converterList, splitter)
    }

    override fun createCustom(
        specialChars: List<Char>,
        customConverters: List<StringToTokenConverter>,
        reader: Reader,
        size: Int,
        splitter: StreamingSplitter,
    ): Lexer {
        val converters = converterFactory.createCustom(customConverters)
        return DefaultLexer(converters, splitter)
    }

    override fun createLexerWithVersion(
        version: Version,
        reader: Reader,
        size: Int,
    ): Lexer {
        val splitter = splitterFactory.createStreamingSplitter(reader, size)
        return when (version) {
            VERSION_1_0 -> createVersionOne(reader, size, splitter)
            VERSION_1_1 -> createVersionOnePointOne(reader, size, splitter)
        }
    }

    override fun createLexerWithVersionAndBufferSize(
        reader: Reader,
        size: Int,
        version: Version,
        splitter: StreamingSplitter,
    ): Lexer =
        when (version) {
            VERSION_1_0 -> createVersionOne(reader, size, splitter)
            VERSION_1_1 -> createVersionOnePointOne(reader, size, splitter)
        }

    fun createLexerWithVersionAndBufferSizeAndStreamingSplitter(
        reader: Reader,
        size: Int,
        version: Version,
        splitter: StreamingSplitter,
    ): Lexer =
        when (version) {
            VERSION_1_0 -> createVersionOne(reader, size, splitter)
            VERSION_1_1 -> createVersionOnePointOne(reader, size, splitter)
        }
}
