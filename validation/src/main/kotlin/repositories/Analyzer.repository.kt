package repositories

import entities.AnalyzerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnalyzerRepository : JpaRepository<AnalyzerEntity, String>
