package services

import utils.ErrorHandler
import utils.InputReceiver
import utils.PrintEmitter
import dtos.AllTestSnippetExecution
import dtos.ExecutionResponseDTO
import dtos.TestExecutionResponseDTO
import org.springframework.stereotype.Service
import repositories.TestRepository
import utils.InterpreterInitializer.execute
import java.io.InputStream

@Service
class ExecutionService(
    private val testRepository: TestRepository,
) {

    fun executeAllTests(
        inputStream: InputStream,
        snippetId: Long,
        version: String,
    ): AllTestSnippetExecution {
        val codeContent = inputStream.readBytes()

        val testResults = mutableListOf<TestExecutionResponseDTO>()

        for (test in testRepository.findBySnippetId(snippetId)) {
            val testResult = executeTest(codeContent.inputStream(), version, test.id)
            testResults.add(testResult)
        }

        return AllTestSnippetExecution(
            executions = testResults,
        )
    }

    fun executeTest(
        inputStream: InputStream,
        version: String,
        testId: Long,
    ): TestExecutionResponseDTO {
        val test =
            testRepository
                .findById(testId)
                .orElseThrow { NoSuchElementException("Test not found: $testId") }

        val outputs = mutableListOf<String>()
        val errors = mutableListOf<String>()
        var currentInputIndex = 0

        val emitter =
            object : PrintEmitter {
                override fun print(message: String?) {
                    message?.let { outputs.add(it) }
                }
            }

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
            execute(inputStream, version, emitter, errorHandler, inputProvider)
        } catch (e: Exception) {
            errors.add(e.message ?: "Unknown error")
        }

        val actualOutputs = outputs.filter { it !in test.inputs }
        val passed = errors.isEmpty() && actualOutputs == test.expectedOutputs

        return TestExecutionResponseDTO(
            testId = testId,
            passed = passed,
            outputs = actualOutputs,
            expectedOutputs = test.expectedOutputs,
            errors = errors,
        )
    }

    fun execute(
        inputStream: InputStream,
        version: String,
        inputs: Map<String, String>,
    ): ExecutionResponseDTO {
        val outputs = mutableListOf<String>()
        val errors = mutableListOf<String>()

        val emitter =
            object : PrintEmitter {
                override fun print(message: String?) {
                    message?.let { outputs.add(it) }
                }
            }

        val inputProvider =
            object : InputReceiver {
                override fun input(name: String?): String? = inputs[name]
            }

        val errorHandler =
            object : ErrorHandler {
                override fun reportError(message: String?) {
                    message?.let { errors.add(it) }
                }
            }

        try {
            execute(inputStream, version, emitter, errorHandler, inputProvider)
        } catch (e: Exception) {
            errors.add(e.message ?: "Unknown error")
        }

        return ExecutionResponseDTO(
            outputs = outputs,
            errors = errors,
            success = errors.isEmpty(),
        )
    }
}
