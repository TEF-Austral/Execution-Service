package repositories

import entities.TestEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestRepository : JpaRepository<TestEntity, Long> {
    fun findBySnippetId(snippetId: Long): List<TestEntity>
}
