package factories

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

class DefaultLexerTokenStreamFactoryTest {

    private val factory = DefaultLexerTokenStreamFactory()

    @Test
    fun `createTokenStream should create stream with version 1_0`() {
        val code = "let x: number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val tokenStream = factory.createTokenStream(inputStream, "1.0")

        assertNotNull(tokenStream)
    }

    @Test
    fun `createTokenStream should create stream with version 1_1`() {
        val code = "const x: number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val tokenStream = factory.createTokenStream(inputStream, "1.1")

        assertNotNull(tokenStream)
    }

    @Test
    fun `createTokenStream should handle complex code`() {
        val code =
            """
            let x: number = 10;
            let y: string = "hello";
            if (x > 5) {
                println(y);
            }
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val tokenStream = factory.createTokenStream(inputStream, "1.1")

        assertNotNull(tokenStream)
    }

    @Test
    fun `createTokenStream should handle empty code`() {
        val code = ""
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val tokenStream = factory.createTokenStream(inputStream, "1.0")

        assertNotNull(tokenStream)
    }

    @Test
    fun `createTokenStream should handle UTF-8 characters`() {
        val code = "let name: string = \"José\";"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val tokenStream = factory.createTokenStream(inputStream, "1.1")

        assertNotNull(tokenStream)
    }
}
