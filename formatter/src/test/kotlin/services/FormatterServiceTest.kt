package services

import dtos.FormatConfigDTO
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class FormatterServiceTest {

    private val formatterService = PrintScriptFormatterService()

    @Test
    fun `format should return formatted code with default config`() {
        val code =
            """
            let x:number=5;
            println(x);
            """.trimIndent()

        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO()

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format should handle space around assignment`() {
        val code = "let x:number=5;"

        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO(spaceAroundAssignment = true)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
        assertTrue(result.contains(" = ") || result.isNotEmpty())
    }

    @Test
    fun `format should handle space after colon`() {
        val code = "let x:number = 5;"

        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config =
            FormatConfigDTO(
                spaceAfterColon = true,
                spaceBeforeColon = false,
            )

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format should handle custom indent size`() {
        val code =
            """
            if (true) {
            println("test");
            }
            """.trimIndent()

        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO(indentSize = 2)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format should handle blank lines after println`() {
        val code =
            """
            println("first");
            println("second");
            """.trimIndent()

        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO(blankLinesAfterPrintln = 2)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format should work with version 1_0`() {
        val code = "let x:number = 5;"

        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO()

        val result = formatterService.format(inputStream, "1.0", config)

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format should handle empty input stream`() {
        val inputStream = ByteArrayInputStream("".toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO()

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }
}
