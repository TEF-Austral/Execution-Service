package factories

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

class DefaultParserTokenStreamFactoryTest {

    private lateinit var factory: DefaultParserTokenStreamFactory

    @BeforeEach
    fun setup() {
        factory = DefaultParserTokenStreamFactory()
    }

    @Test
    fun testCreateParserWithVersion10() {
        val code = "let x: number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val parser = factory.createParser(inputStream, "1.0")

        assertNotNull(parser)
    }

    @Test
    fun testCreateParserWithVersion11() {
        val code = "let x: number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val parser = factory.createParser(inputStream, "1.1")

        assertNotNull(parser)
    }

    @Test
    fun testCreateParserWithEmptyInput() {
        val code = ""
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val parser = factory.createParser(inputStream, "1.0")

        assertNotNull(parser)
    }

    @Test
    fun testCreateParserWithComplexCode() {
        val code =
            """
            let x: number = 5;
            let y: string = "hello";
            println(x);
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val parser = factory.createParser(inputStream, "1.1")

        assertNotNull(parser)
    }

    @Test
    fun testCreateParserHandlesUtf8() {
        val code = "let x: string = \"hello world\";"
        val inputStream = ByteArrayInputStream(code.toByteArray(Charsets.UTF_8))

        val parser = factory.createParser(inputStream, "1.0")

        assertNotNull(parser)
    }
}
