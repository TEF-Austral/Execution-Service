package api.dtos

import api.entities.Language

data class UpdateSnippetRequestDTO(
    val name: String?,
    val content: String?,
    val language: Language?,
    val version: String?,
)
