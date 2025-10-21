package api.repositories

import api.entities.Snippet
import api.entities.Test
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SnippetRepository : JpaRepository<Snippet, Long> {
    fun findByDeletedAtIsNull(): List<Snippet>

    fun findByIdAndDeletedAtIsNull(id: Long): Snippet?
}

@Repository
interface TestRepository : JpaRepository<Test, Long> {
    fun findBySnippetId(snippetId: Long): List<Test>
}

@Repository
class PostgresExecutionRepository(
    private val snippetRepository: SnippetRepository,
    private val testRepository: TestRepository,
) : ExecutionRepository {

    override fun saveSnippet(snippet: Snippet): Snippet = snippetRepository.save(snippet)

    override fun findAllSnippets(): List<Snippet> = snippetRepository.findByDeletedAtIsNull()

    override fun findSnippetById(id: Long): Snippet? = snippetRepository.findById(id).orElse(null)

    override fun findSnippetByIdNotDeleted(id: Long): Snippet? =
        snippetRepository.findByIdAndDeletedAtIsNull(id)

    override fun deleteSnippet(snippet: Snippet): Snippet = snippetRepository.save(snippet)

    override fun saveTest(test: Test): Test = testRepository.save(test)

    override fun findTestsBySnippetId(snippetId: Long): List<Test> =
        testRepository.findBySnippetId(snippetId)
}
