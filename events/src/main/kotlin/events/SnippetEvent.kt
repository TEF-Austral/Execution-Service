package com.github.tef.events

data class SnippetEvent(
    val snippetId: Long,
    val ownerId: String,
    val name: String,
    val content: String?,
    val language: String,
    val version: String,
    val operation: SnippetOperation,
)
