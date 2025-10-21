package api.dtos

import api.entities.Language
import java.time.LocalDateTime

data class SnippetDTO(
    val name: String,
    val content: String,
    val deletedAt: LocalDateTime? = null,
    val language: Language,
    val version: String,
    val tests: List<TestDTO> = emptyList(),
)
