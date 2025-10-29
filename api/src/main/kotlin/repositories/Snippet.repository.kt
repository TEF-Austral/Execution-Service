package api.repositories

import api.entities.Snippet
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface SnippetRepository : JpaRepository<Snippet, Long> {
    override fun findById(id: Long): Optional<Snippet>
}
