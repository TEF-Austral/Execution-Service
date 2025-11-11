package services

import utils.ErrorHandler
import utils.InputReceiver
import dtos.AllTestSnippetExecution
import dtos.TestExecutionResponseDTO
import org.springframework.stereotype.Service
import repositories.TestRepository
import utils.FakeEmitter
import utils.CapturingPrintEmitter
import utils.InterpreterInitializer.execute
import java.io.InputStream

@Service
class ExecutionService(
    private val testRepository: TestRepository,
) {
    private val log = org.slf4j.LoggerFactory.getLogger(ExecutionService::class.java)

    fun executeAllTests(
        inputStream: InputStream,
        snippetId: Long,
        version: String,
    ): AllTestSnippetExecution {
        log.info("Executing all tests for snippet $snippetId, version $version")
        val codeContent = inputStream.readBytes()

        val testResults = mutableListOf<TestExecutionResponseDTO>()

        for (test in testRepository.findBySnippetId(snippetId)) {
            val testResult = executeTest(codeContent.inputStream(), version, test.id)
            testResults.add(testResult)
        }

        log.warn("Executed ${testResults.size} tests for snippet $snippetId")
        return AllTestSnippetExecution(
            executions = testResults,
        )
    }

    fun executeTest(
        inputStream: InputStream,
        version: String,
        testId: Long,
    ): TestExecutionResponseDTO {
        log.info("Executing test $testId, version $version")
        val outputs = mutableListOf<String>()
        val printEmitter = CapturingPrintEmitter(outputs)
        val inputEmitter = FakeEmitter()

        val result = execute(inputStream, version, testId, printEmitter, inputEmitter, outputs)
        log.warn("Test $testId executed, passed: ${result.passed}")
        return result
    }

    private fun execute(
        inputStream: InputStream,
        version: String,
        testId: Long,
        printEmitter: emitter.Emitter,
        inputEmitter: emitter.Emitter,
        outputs: MutableList<String>,
    ): TestExecutionResponseDTO {
        val test =
            testRepository
                .findById(testId)
                .orElseThrow { NoSuchElementException("Test not found: $testId") }
        val errors = mutableListOf<String>()
        var currentInputIndex = 0

        val inputProvider =
            object : InputReceiver {
                override fun input(name: String?): String? =
                    if (currentInputIndex < test.inputs.size) {
                        test.inputs[currentInputIndex++]
                    } else {
                        null
                    }
            }

        val errorHandler =
            object : ErrorHandler {
                override fun reportError(message: String?) {
                    message?.let { errors.add(it) }
                }
            }

        try {
            execute(inputStream, version, printEmitter, inputEmitter, errorHandler, inputProvider)
        } catch (e: Exception) {
            errors.add(e.message ?: "Unknown error")
        }

        val passed =
            errors.isEmpty() &&
                outputs.size == test.expectedOutputs.size &&
                outputs.zip(test.expectedOutputs).all { (actual, expected) ->
                    actual.trim() == expected.trim()
                }

        return TestExecutionResponseDTO(
            testId = testId,
            passed = passed,
            outputs = outputs,
            expectedOutputs = test.expectedOutputs,
            errors = errors,
        )
    }
}
