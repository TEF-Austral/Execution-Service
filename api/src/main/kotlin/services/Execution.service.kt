package api.services

import api.ExecutePrintScript
import api.dtos.CreateSnippetRequest
import api.dtos.CreateTestRequest
import api.dtos.ExecutionResult
import api.dtos.SnippetDTO
import api.dtos.TestDTO
import api.entities.Language
import api.entities.Snippet
import api.entities.Test
import api.repositories.ExecutionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ExecutionService(
    private val executionRepository: ExecutionRepository,
) {

    @Transactional
    fun createSnippet(request: CreateSnippetRequest): SnippetDTO {
        val snippet =
            Snippet(
                id = 0,
                name = request.name,
                content = request.content,
                language = request.language,
                version = request.version,
            )
        val saved = executionRepository.saveSnippet(snippet)
        return toDTO(saved)
    }

    @Transactional
    fun createTest(request: CreateTestRequest): TestDTO {
        val snippet =
            executionRepository.findSnippetByIdNotDeleted(request.mainCodeId)
                ?: throw IllegalArgumentException("Main snippet not found")

        val test =
            Test(
                id = 0,
                snippet = snippet,
                inputs = request.inputs,
                outputs = request.outputs,
                expectedOutcomeIsSuccess = request.expectedOutcomeIsSuccess,
            )
        val saved = executionRepository.saveTest(test)
        return toTestDTO(saved)
    }

    fun getAllSnippets(): List<SnippetDTO> = executionRepository.findAllSnippets().map { toDTO(it) }

    fun getSnippetById(id: Long): SnippetDTO? =
        executionRepository.findSnippetByIdNotDeleted(id)?.let {
            toDTO(it)
        }

    @Transactional
    fun deleteSnippet(id: Long) {
        val snippet =
            executionRepository.findSnippetByIdNotDeleted(id)
                ?: throw IllegalArgumentException("Snippet not found")

        val updated = snippet.copy(deletedAt = LocalDateTime.now())
        executionRepository.deleteSnippet(updated)
    }

    fun executeSnippet(snippetId: Long): ExecutionResult {
        val snippet =
            executionRepository.findSnippetByIdNotDeleted(snippetId)
                ?: return ExecutionResult(false, "Snippet not found")

        return when (snippet.language) {
            Language.PRINTSCRIPT -> ExecutePrintScript().execute(snippet.content, snippet.version)
        }
    }

    private fun toDTO(snippet: Snippet): SnippetDTO =
        SnippetDTO(
            name = snippet.name,
            content = snippet.content,
            deletedAt = snippet.deletedAt,
            language = snippet.language,
            version = snippet.version,
            tests = snippet.tests.map { toTestDTO(it) },
        )

    private fun toTestDTO(test: Test): TestDTO =
        TestDTO(
            mainCodeId = test.snippet.id,
            inputs = test.inputs,
            outputs = test.outputs,
            expectedOutcomeIsSuccess = test.expectedOutcomeIsSuccess,
        )
}
