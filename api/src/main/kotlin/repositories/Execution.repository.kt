package api.repositories

import api.entities.Snippet
import api.entities.Test

interface ExecutionRepository {
    fun saveSnippet(snippet: Snippet): Snippet

    fun findSnippetById(id: Long): Snippet?

    fun deleteSnippet(snippet: Snippet): Snippet

    fun saveTest(test: Test): Test

    fun findTestsBySnippetId(snippetId: Long): List<Test>
}
