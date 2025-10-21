import api.dtos.CreateSnippetRequest
import api.entities.Language
import api.repositories.MockExecutionRepository
import api.services.ExecutionService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class SnippetExecutionFromFilesTest {

    private lateinit var repository: MockExecutionRepository
    private lateinit var service: ExecutionService

    @BeforeEach
    fun setUp() {
        repository = MockExecutionRepository()
        service = ExecutionService(repository)
    }

    @AfterEach
    fun tearDown() {
        repository.clear()
    }

    @Test
    fun execute_valid_files_v1_0() {
        executeFilesUnder(
            "src/test/resources/interpreter/v1_0",
            prefix = "valid_",
            expectedSuccess = true,
            version = "1.0",
        )
    }

    @Test
    fun execute_invalid_files_v1_0() {
        executeFilesUnder(
            "src/test/resources/interpreter/v1_0",
            prefix = "invalid_",
            expectedSuccess = false,
            version = "1.0",
        )
    }

    @Test
    fun execute_valid_files_v1_1() {
        executeFilesUnder(
            "src/test/resources/interpreter/v1_1",
            prefix = "valid_",
            expectedSuccess = true,
            version = "1.1",
        )
    }

    @Test
    fun execute_invalid_files_v1_1() {
        executeFilesUnder(
            "src/test/resources/interpreter/v1_1",
            prefix = "invalid_",
            expectedSuccess = false,
            version = "1.1",
        )
    }

    private fun executeFilesUnder(
        baseDir: String,
        prefix: String,
        expectedSuccess: Boolean,
        version: String,
    ) {
        val dir = Paths.get(baseDir)
        require(Files.exists(dir)) { "Directory not found: $baseDir" }

        Files.list(dir).use { pathsStream ->
            val files =
                pathsStream
                    .filter {
                        Files.isRegularFile(it) &&
                            it.fileName.toString().startsWith(prefix) &&
                            it.fileName.toString().endsWith(".txt")
                    }.sorted()
                    .toList()

            require(files.isNotEmpty()) { "No test files found in $baseDir with prefix $prefix" }

            files.forEach { path ->
                val code = readFile(path)
                val name = path.fileName.toString()

                service.createSnippet(
                    CreateSnippetRequest(
                        name = name,
                        content = code,
                        language = Language.PRINTSCRIPT,
                        version = version,
                    ),
                )

                val saved =
                    repository.findAllSnippets().lastOrNull { it.name == name }
                        ?: error("Snippet not found after saving: $name")

                val result = service.executeSnippet(saved.id)

                if (expectedSuccess) {
                    assertTrue(
                        result.wasSuccessful,
                        "Expected success for $name ($version), but got: ${result.result}",
                    )
                } else {
                    assertFalse(
                        result.wasSuccessful,
                        "Expected failure for $name ($version), but it succeeded",
                    )
                }
            }
        }
    }

    private fun readFile(path: Path): String = Files.readString(path, StandardCharsets.UTF_8)
}
