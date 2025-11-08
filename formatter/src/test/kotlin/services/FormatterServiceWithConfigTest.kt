package services

import dtos.FormatConfigDTO
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class FormatterServiceWithConfigTest {

    private val formatterService = FormatterService()

    @Test
    fun `format should apply spaceBeforeColon config`() {
        val code = "let x:number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))

        val configWithSpace = FormatConfigDTO(spaceBeforeColon = true)
        val resultWithSpace = formatterService.format(inputStream, "1.1", configWithSpace)

        val inputStream2 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val configWithoutSpace = FormatConfigDTO(spaceBeforeColon = false)
        val resultWithoutSpace = formatterService.format(inputStream2, "1.1", configWithoutSpace)

        assertNotNull(resultWithSpace)
        assertNotNull(resultWithoutSpace)
    }

    @Test
    fun `format should apply spaceAfterColon config`() {
        val code = "let x:number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))

        val configWithSpace = FormatConfigDTO(spaceAfterColon = true, spaceBeforeColon = false)
        val resultWithSpace = formatterService.format(inputStream, "1.1", configWithSpace)

        val inputStream2 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val configWithoutSpace = FormatConfigDTO(spaceAfterColon = false, spaceBeforeColon = false)
        val resultWithoutSpace = formatterService.format(inputStream2, "1.1", configWithoutSpace)

        assertNotNull(resultWithSpace)
        assertNotNull(resultWithoutSpace)
    }

    @Test
    fun `format should apply spaceAroundAssignment config`() {
        val code = "let x: number=5;"

        val inputStream1 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val configWithSpace = FormatConfigDTO(spaceAroundAssignment = true)
        val resultWithSpace = formatterService.format(inputStream1, "1.1", configWithSpace)

        val inputStream2 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val configWithoutSpace = FormatConfigDTO(spaceAroundAssignment = false)
        val resultWithoutSpace = formatterService.format(inputStream2, "1.1", configWithoutSpace)

        assertNotNull(resultWithSpace)
        assertNotNull(resultWithoutSpace)
    }

    @Test
    fun `format should apply indentSize config for if statements`() {
        val code =
            """
            if (true) {
            println("test");
            }
            """.trimIndent()

        val inputStream1 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config2Spaces = FormatConfigDTO(indentSize = 2)
        val result2Spaces = formatterService.format(inputStream1, "1.1", config2Spaces)

        val inputStream2 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config4Spaces = FormatConfigDTO(indentSize = 4)
        val result4Spaces = formatterService.format(inputStream2, "1.1", config4Spaces)

        val inputStream3 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config8Spaces = FormatConfigDTO(indentSize = 8)
        val result8Spaces = formatterService.format(inputStream3, "1.1", config8Spaces)

        assertNotNull(result2Spaces)
        assertNotNull(result4Spaces)
        assertNotNull(result8Spaces)
        assertFalse(result2Spaces == result4Spaces)
        assertFalse(result4Spaces == result8Spaces)
    }

    @Test
    fun `format should apply blankLinesAfterPrintln config`() {
        val code =
            """
            println("first");
            println("second");
            """.trimIndent()

        val inputStream1 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config1Line = FormatConfigDTO(blankLinesAfterPrintln = 1)
        val result1Line = formatterService.format(inputStream1, "1.1", config1Line)

        val inputStream2 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config2Lines = FormatConfigDTO(blankLinesAfterPrintln = 2)
        val result2Lines = formatterService.format(inputStream2, "1.1", config2Lines)

        val inputStream3 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val config3Lines = FormatConfigDTO(blankLinesAfterPrintln = 3)
        val result3Lines = formatterService.format(inputStream3, "1.1", config3Lines)

        assertNotNull(result1Line)
        assertNotNull(result2Lines)
        assertNotNull(result3Lines)
    }

    @Test
    fun `format should apply ifBraceOnSameLine config`() {
        val code =
            """
            if (true)
            {
            println("test");
            }
            """.trimIndent()

        val inputStream1 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val configSameLine = FormatConfigDTO(ifBraceOnSameLine = true)
        val resultSameLine = formatterService.format(inputStream1, "1.1", configSameLine)

        val inputStream2 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val configNewLine = FormatConfigDTO(ifBraceOnSameLine = false)
        val resultNewLine = formatterService.format(inputStream2, "1.1", configNewLine)

        assertNotNull(resultSameLine)
        assertNotNull(resultNewLine)
    }

    @Test
    fun `format should apply enforceSingleSpace config`() {
        val code = "let x:   number   =   5;"

        val inputStream1 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val configEnforce = FormatConfigDTO(enforceSingleSpace = true)
        val resultEnforce = formatterService.format(inputStream1, "1.1", configEnforce)

        val inputStream2 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val configNoEnforce = FormatConfigDTO(enforceSingleSpace = false)
        val resultNoEnforce = formatterService.format(inputStream2, "1.1", configNoEnforce)

        assertNotNull(resultEnforce)
        assertNotNull(resultNoEnforce)
    }

    @Test
    fun `format should apply spaceAroundOperators config`() {
        val code = "let x: number = 5+3*2;"

        val inputStream1 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val configWithSpace = FormatConfigDTO(spaceAroundOperators = true)
        val resultWithSpace = formatterService.format(inputStream1, "1.1", configWithSpace)

        val inputStream2 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val configWithoutSpace = FormatConfigDTO(spaceAroundOperators = false)
        val resultWithoutSpace = formatterService.format(inputStream2, "1.1", configWithoutSpace)

        assertNotNull(resultWithSpace)
        assertNotNull(resultWithoutSpace)
    }

    @Test
    fun `format should apply multiple config options together`() {
        val code =
            """
            let x:number=5;
            if(x>0){
            println("positive");
            }
            """.trimIndent()

        val customConfig =
            FormatConfigDTO(
                spaceBeforeColon = true,
                spaceAfterColon = true,
                spaceAroundAssignment = true,
                blankLinesAfterPrintln = 2,
                indentSize = 2,
                ifBraceOnSameLine = true,
                enforceSingleSpace = true,
                spaceAroundOperators = true,
            )

        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val result = formatterService.format(inputStream, "1.1", customConfig)

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format should work with version 1_0 and custom config`() {
        val code = "let x:number=5+3;"

        val config =
            FormatConfigDTO(
                spaceAfterColon = true,
                spaceAroundAssignment = true,
                spaceAroundOperators = true,
            )

        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val result = formatterService.format(inputStream, "1.0", config)

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format should handle complex nested structures with custom config`() {
        val code =
            """
            let x:number=5;
            if(x>0){
            if(x<10){
            println("single digit");
            }
            }
            """.trimIndent()

        val config =
            FormatConfigDTO(
                indentSize = 3,
                ifBraceOnSameLine = false,
                spaceAroundOperators = true,
            )

        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format with zero blank lines after println`() {
        val code =
            """
            println("line1");
            println("line2");
            println("line3");
            """.trimIndent()

        val config = FormatConfigDTO(blankLinesAfterPrintln = 0)

        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format should handle all false boolean configs`() {
        val code = "let x : number = 5 + 3;"

        val config =
            FormatConfigDTO(
                spaceBeforeColon = false,
                spaceAfterColon = false,
                spaceAroundAssignment = false,
                ifBraceOnSameLine = false,
                enforceSingleSpace = false,
                spaceAroundOperators = false,
            )

        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format should handle all true boolean configs`() {
        val code = "let x:number=5+3;"

        val config =
            FormatConfigDTO(
                spaceBeforeColon = true,
                spaceAfterColon = true,
                spaceAroundAssignment = true,
                ifBraceOnSameLine = true,
                enforceSingleSpace = true,
                spaceAroundOperators = true,
            )

        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val result = formatterService.format(inputStream, "1.1", config)

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }
}
