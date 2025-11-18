package services

import dtos.FormatConfigDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

class PrintScriptFormatterServiceTest {

    private val formatterService = PrintScriptFormatterService()

    @Test
    fun `supportsLanguage should return PRINTSCRIPT`() {
        assertEquals("PRINTSCRIPT", formatterService.supportsLanguage())
    }

    @Test
    fun `format should handle space before colon`() {
        val code = "let x:number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val config = FormatConfigDTO(spaceBeforeColon = true)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle no space before colon`() {
        val code = "let x : number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val config = FormatConfigDTO(spaceBeforeColon = false)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle no space around assignment`() {
        val code = "let x: number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val config = FormatConfigDTO(spaceAroundAssignment = false)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle brace below line`() {
        val code = "if (true) { println(\"test\"); }"
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val config = FormatConfigDTO(ifBraceOnSameLine = false)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle multiple blank lines after println`() {
        val code = "println(\"test\");\nprintln(\"test2\");"
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val config = FormatConfigDTO(blankLinesAfterPrintln = 3)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should disable enforce single space`() {
        val code = "let  x:  number  =  5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val config = FormatConfigDTO(enforceSingleSpace = false)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle space around operators`() {
        val code = "let x: number = 5+3*2;"
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val config = FormatConfigDTO(spaceAroundOperators = true)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle no space around operators`() {
        val code = "let x: number = 5 + 3 * 2;"
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val config = FormatConfigDTO(spaceAroundOperators = false)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle complex if statements`() {
        val code =
            """
            if (x > 5) {
                if (y < 10) {
                    println("nested");
                }
            }
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val config = FormatConfigDTO(indentSize = 2)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle const declarations in version 1_1`() {
        val code = "const x:number=5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val config = FormatConfigDTO()

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }
}
