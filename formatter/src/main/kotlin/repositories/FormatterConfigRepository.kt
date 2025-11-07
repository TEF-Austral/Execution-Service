package repositories

import entities.FormatterConfigEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface FormatterConfigRepository : JpaRepository<FormatterConfigEntity, Long> {
    fun findByUserId(userId: String): Optional<FormatterConfigEntity>
}
