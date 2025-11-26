package services

import Language
import dtos.AllTestSnippetExecution
import dtos.TestExecutionResponseDTO
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class LanguagesExecutionService(
    private val executionServices: List<LanguageExecutionService>,
) {
    private val log = org.slf4j.LoggerFactory.getLogger(LanguagesExecutionService::class.java)

    fun executeAllTests(
        inputStream: InputStream,
        snippetId: Long,
        version: String,
        language: Language,
    ): AllTestSnippetExecution {
        log.info("Executing all tests for language $language, snippet $snippetId, version $version")
        val service =
            executionServices.find { it.supportsLanguage() == language.name }
                ?: throw IllegalArgumentException("Unsupported language: $language")
        return service.executeAllTests(inputStream, snippetId, version)
    }

    fun executeTest(
        inputStream: InputStream,
        version: String,
        testId: Long,
        language: Language,
    ): TestExecutionResponseDTO {
        log.info("Executing test for language $language, test $testId, version $version")
        val service =
            executionServices.find { it.supportsLanguage() == language.name }
                ?: throw IllegalArgumentException("Unsupported language: $language")
        return service.executeTest(inputStream, version, testId)
    }
}
