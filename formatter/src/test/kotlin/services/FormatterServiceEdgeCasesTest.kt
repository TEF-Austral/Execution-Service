package services

import dtos.FormatConfigDTO
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class FormatterServiceEdgeCasesTest {

    private val formatterService = PrintScriptFormatterService()

    @Test
    fun `format should handle empty code`() {
        val code = ""
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO()

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle single statement`() {
        val code = "let x: number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO()

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle nested if statements`() {
        val code =
            """
            if (true) {
                if (false) {
                    println("nested");
                }
            }
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO(indentSize = 2)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle long expressions`() {
        val code = "let result: number = 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10;"
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO(spaceAroundOperators = true)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle string literals with spaces`() {
        val code = "let message: string = \"Hello   World\";"
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO()

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle multiple variable declarations`() {
        val code =
            """
            let x: number = 1;
            let y: number = 2;
            let z: number = 3;
            let sum: number = x + y + z;
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO()

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle const in version 1_1`() {
        val code =
            """
            const PI: number = 3.14;
            const NAME: string = "Circle";
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO()

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle zero blank lines after println`() {
        val code =
            """
            println("line1");
            println("line2");
            println("line3");
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO(blankLinesAfterPrintln = 0)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle large indent size`() {
        val code =
            """
            if (true) {
                println("indented");
            }
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO(indentSize = 8)

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle all boolean options false`() {
        val code = "let x : number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config =
            FormatConfigDTO(
                spaceBeforeColon = false,
                spaceAfterColon = false,
                spaceAroundAssignment = false,
                ifBraceOnSameLine = false,
                enforceSingleSpace = false,
                spaceAroundOperators = false,
            )

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle all boolean options true`() {
        val code = "let x:number=5;"
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config =
            FormatConfigDTO(
                spaceBeforeColon = true,
                spaceAfterColon = true,
                spaceAroundAssignment = true,
                ifBraceOnSameLine = true,
                enforceSingleSpace = true,
                spaceAroundOperators = true,
            )

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle mixed operators`() {
        val code = "let result: number = 10 + 5 * 2 - 3 / 1;"
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO(spaceAroundOperators = true)

        val result = formatterService.format(inputStream, "1.0", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle parentheses in expressions`() {
        val code = "let result: number = (10 + 5) * 2;"
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO()

        val result = formatterService.format(inputStream, "1.0", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle readInput in version 1_1`() {
        val code = "let name: string = readInput(\"Enter name:\");"
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO()

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle string with special characters`() {
        val code = "let text: string = \"Hello\\nWorld\";"
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO()

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }

    @Test
    fun `format should handle boolean literals`() {
        val code =
            """
            let isTrue: boolean = true;
            let isFalse: boolean = false;
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config = FormatConfigDTO()

        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
    }
}
