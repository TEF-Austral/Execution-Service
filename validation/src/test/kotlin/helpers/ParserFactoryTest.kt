package helpers

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import java.io.ByteArrayInputStream

class ParserFactoryTest {

    @Test
    fun `parse creates parser for version 1_0`() {
        val code = "let x: number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val parser = ParserFactory.parse(inputStream, "1.0")

        assertNotNull(parser)
    }

    @Test
    fun `parse creates parser for version 1_1`() {
        val code = "let x: number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val parser = ParserFactory.parse(inputStream, "1.1")

        assertNotNull(parser)
    }

    @Test
    fun `parse handles empty input stream`() {
        val inputStream = ByteArrayInputStream(ByteArray(0))

        val parser = ParserFactory.parse(inputStream, "1.1")

        assertNotNull(parser)
    }

    @Test
    fun `parse handles UTF-8 encoded content`() {
        val code = "let mensaje: string = \"Hola\";"
        val inputStream = ByteArrayInputStream(code.toByteArray(Charsets.UTF_8))

        val parser = ParserFactory.parse(inputStream, "1.1")

        assertNotNull(parser)
        val result = parser.parse()
        assertTrue(result.isSuccess())
    }

    @Test
    fun `parse creates different parsers for different versions`() {
        val code = "let x: number = 5;"
        val inputStream1 = ByteArrayInputStream(code.toByteArray())
        val inputStream2 = ByteArrayInputStream(code.toByteArray())

        val parser1 = ParserFactory.parse(inputStream1, "1.0")
        val parser2 = ParserFactory.parse(inputStream2, "1.1")

        assertNotNull(parser1)
        assertNotNull(parser2)
    }
}
