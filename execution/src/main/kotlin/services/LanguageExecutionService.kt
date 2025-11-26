package services

import dtos.AllTestSnippetExecution
import dtos.TestExecutionResponseDTO
import java.io.InputStream

interface LanguageExecutionService {
    fun executeAllTests(
        inputStream: InputStream,
        snippetId: Long,
        version: String,
    ): AllTestSnippetExecution

    fun executeTest(
        inputStream: InputStream,
        version: String,
        testId: Long,
    ): TestExecutionResponseDTO

    fun supportsLanguage(): String
}
