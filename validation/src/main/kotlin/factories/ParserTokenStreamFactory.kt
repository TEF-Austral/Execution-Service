package factories

import parser.ParserInterface
import java.io.InputStream

interface ParserTokenStreamFactory {
    fun createParser(
        src: InputStream,
        version: String,
    ): ParserInterface
}
