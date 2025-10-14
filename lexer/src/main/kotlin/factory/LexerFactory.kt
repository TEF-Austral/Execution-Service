package factory

import Lexer
import converter.specific.StringToTokenConverter
import java.io.Reader
import kotlin.Int
import string.streaming.StreamingSplitter
import type.Version

interface LexerFactory {
    fun createVersionOne(
        reader: Reader,
        size: Int,
        splitter: StreamingSplitter,
    ): Lexer

    fun createVersionOnePointOne(
        reader: Reader,
        size: Int = 65536,
        splitter: StreamingSplitter,
    ): Lexer

    fun createCustom(
        specialChars: List<Char>,
        customConverters: List<StringToTokenConverter>,
        reader: Reader,
        size: Int = 65536,
        splitter: StreamingSplitter,
    ): Lexer

    fun createLexerWithVersion(
        version: Version,
        reader: Reader,
        size: Int = 65536,
    ): Lexer

    fun createLexerWithVersionAndBufferSize(
        reader: Reader,
        size: Int = 65536,
        version: Version,
        splitter: StreamingSplitter,
    ): Lexer
}
