package api.repositories

import api.entities.Snippet
import api.entities.Test
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("mock")
class MockExecutionRepository : ExecutionRepository {
    private val snippets = mutableMapOf<Long, Snippet>()
    private val tests = mutableMapOf<Long, Test>()
    private var snippetIdCounter = 1L
    private var testIdCounter = 1L

    override fun saveSnippet(snippet: Snippet): Snippet {
        val id = snippetIdCounter++
        val savedSnippet = snippet.copy(id = id)
        snippets[id] = savedSnippet
        return savedSnippet
    }

    override fun findAllSnippets(): List<Snippet> = snippets.values.filter { it.deletedAt == null }

    override fun findSnippetById(id: Long): Snippet? = snippets[id]

    override fun findSnippetByIdNotDeleted(id: Long): Snippet? =
        snippets[id]?.takeIf {
            it.deletedAt == null
        }

    override fun deleteSnippet(snippet: Snippet): Snippet {
        snippet.id.let { snippets[it] = snippet }
        return snippet
    }

    override fun saveTest(test: Test): Test {
        val id = testIdCounter++
        val savedTest = test.copy(id = id)
        tests[id] = savedTest
        return savedTest
    }

    override fun findTestsBySnippetId(snippetId: Long): List<Test> =
        tests.values.filter {
            it.snippet.id == snippetId
        }

    fun clear() {
        snippets.clear()
        tests.clear()
        snippetIdCounter = 1L
        testIdCounter = 1L
    }
}
