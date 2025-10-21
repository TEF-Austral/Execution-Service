import api.repositories.ExecutionRepository
import api.services.ExecutionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

data class TestResult(
    val testName: String,
    val success: Boolean,
    val message: String,
)

data class TestSuiteResult(
    val totalTests: Int,
    val passedTests: Int,
    val failedTests: Int,
    val results: List<TestResult>,
)

@RestController
@RequestMapping("/api/tests")
class ExecutionTestController(
    private val executionService: ExecutionService,
    private val executionRepository: ExecutionRepository,
) {

    @GetMapping("/run")
    fun runTests(): ResponseEntity<TestSuiteResult> {
        val results = mutableListOf<TestResult>()

        results.addAll(
            executeTestSuite("src/test/resources/interpreter/v1_0", "valid_", true, "1.0"),
        )
        results.addAll(
            executeTestSuite("src/test/resources/interpreter/v1_0", "invalid_", false, "1.0"),
        )
        results.addAll(
            executeTestSuite("src/test/resources/interpreter/v1_1", "valid_", true, "1.1"),
        )
        results.addAll(
            executeTestSuite("src/test/resources/interpreter/v1_1", "invalid_", false, "1.1"),
        )

        val passedTests = results.count { it.success }
        val failedTests = results.count { !it.success }

        val suiteResult =
            TestSuiteResult(
                totalTests = results.size,
                passedTests = passedTests,
                failedTests = failedTests,
                results = results,
            )

        return ResponseEntity.ok(suiteResult)
    }

    private fun executeTestSuite(
        baseDir: String,
        prefix: String,
        expectedSuccess: Boolean,
        version: String,
    ): List<TestResult> {
        val results = mutableListOf<TestResult>()
        val dir = Paths.get(baseDir)

        if (!Files.exists(dir)) {
            results.add(
                TestResult(
                    testName = "$baseDir ($prefix)",
                    success = false,
                    message = "Directory not found: $baseDir",
                ),
            )
            return results
        }

        Files.list(dir).use { pathsStream ->
            val files =
                pathsStream
                    .filter {
                        Files.isRegularFile(it) &&
                            it.fileName.toString().startsWith(prefix) &&
                            it.fileName.toString().endsWith(".txt")
                    }.sorted()
                    .toList()

            if (files.isEmpty()) {
                results.add(
                    TestResult(
                        testName = "$baseDir ($prefix)",
                        success = false,
                        message = "No test files found with prefix $prefix",
                    ),
                )
                return results
            }

            files.forEach { path ->
                val testResult = executeTestFile(path, expectedSuccess, version)
                results.add(testResult)
            }
        }

        return results
    }

    private fun executeTestFile(
        path: Path,
        expectedSuccess: Boolean,
        version: String,
    ): TestResult =
        try {
            val code = Files.readString(path, StandardCharsets.UTF_8)
            val name = path.fileName.toString()

            executionService.createSnippet(
                api.dtos.CreateSnippetRequest(
                    name = name,
                    content = code,
                    language = api.entities.Language.PRINTSCRIPT,
                    version = version,
                ),
            )

            val saved =
                executionRepository.findAllSnippets().lastOrNull { it.name == name }
                    ?: throw IllegalStateException("Snippet not found after saving: $name")

            val executionResult = executionService.executeSnippet(saved.id)

            val success = executionResult.wasSuccessful == expectedSuccess
            val message =
                if (success) {
                    "Passed - Expected ${if (expectedSuccess) "success" else "failure"} and got ${if (executionResult.wasSuccessful) "success" else "failure"}"
                } else {
                    "Failed - Expected ${if (expectedSuccess) "success" else "failure"} but got ${if (executionResult.wasSuccessful) "success" else "failure"}. Result: ${executionResult.result}"
                }

            TestResult(
                testName = "$name (v$version)",
                success = success,
                message = message,
            )
        } catch (e: Exception) {
            TestResult(
                testName = "${path.fileName} (v$version)",
                success = false,
                message = "Exception: ${e.message}",
            )
        }
}
