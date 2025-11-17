package utils

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

class ParserInitializerTest {

    @Test
    fun `parse should create parser with version 1_0`() {
        val code = "let x: number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val parser = ParserInitializer.parse(inputStream, "1.0")

        assertNotNull(parser)
    }

    @Test
    fun `parse should create parser with version 1_1`() {
        val code = "let x: number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val parser = ParserInitializer.parse(inputStream, "1.1")

        assertNotNull(parser)
    }

    @Test
    fun `parse should handle complex code`() {
        val code =
            """
            let x: number = 10;
            let y: string = "hello";
            println(x);
            println(y);
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val parser = ParserInitializer.parse(inputStream, "1.1")

        assertNotNull(parser)
    }
}
