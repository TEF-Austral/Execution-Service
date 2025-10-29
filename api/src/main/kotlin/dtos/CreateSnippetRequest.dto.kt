package api.dtos

import api.entities.Language

data class CreateSnippetRequest(
    val name: String,
    val content: String,
    val language: Language,
    val version: String,
)
