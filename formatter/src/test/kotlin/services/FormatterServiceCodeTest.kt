package services

import dtos.FormatConfigDTO
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class FormatterServiceCodeTest {

    private val formatterService = FormatterService()

    private fun formatAllIn(
        versionDir: String,
        versionString: String,
        config: FormatConfigDTO,
    ) {
        val dirUrl =
            javaClass.classLoader.getResource("printscript/$versionDir")
                ?: throw IllegalStateException(
                    "Resource directory not found: printscript/$versionDir",
                )
        val dirPath = Paths.get(dirUrl.toURI())

        var count = 0
        Files.list(dirPath).use { stream ->
            stream
                .filter { Files.isRegularFile(it) && it.toString().endsWith(".txt") }
                .forEach { path ->
                    count++
                    Files.newInputStream(path).use { inputStream ->
                        val result = formatterService.format(inputStream, versionString, config)
                        assertNotNull(result, "Result is null for ${path.fileName}")
                        assertTrue(result.isNotEmpty(), "Result is empty for ${path.fileName}")
                    }
                }
        }

        assertTrue(count > 0, "No .txt files found in printscript/$versionDir")
    }

    @Test
    fun `format all v1_0 resource files`() {
        formatAllIn(
            versionDir = "v1_0",
            versionString = "1.0",
            config =
                FormatConfigDTO(
                    spaceAfterColon = true,
                    spaceAroundAssignment = true,
                    spaceAroundOperators = true,
                    enforceSingleSpace = true,
                ),
        )
    }

    @Test
    fun `format all v1_1 resource files`() {
        formatAllIn(
            versionDir = "v1_1",
            versionString = "1.1",
            config =
                FormatConfigDTO(
                    indentSize = 2,
                    ifBraceOnSameLine = true,
                    spaceAroundOperators = true,
                ),
        )
    }
}
