package com.github.tef

import com.github.tef.events.SnippetEvent
import com.github.tef.events.SnippetOperation
import org.springframework.stereotype.Service

@Service
class SnippetEventHandler {

    fun handleSnippetEvent(event: SnippetEvent) {
        println("🔔 Processing snippet event: ${event.operation} for snippet ${event.snippetId}")

        when (event.operation) {
            SnippetOperation.CREATE -> handleCreate(event)
            SnippetOperation.UPDATE -> handleUpdate(event)
            SnippetOperation.DELETE -> handleDelete(event)
        }
    }

    private fun handleCreate(event: SnippetEvent) {
        println("📝 New snippet created:")
        println("   ID: ${event.snippetId}")
        println("   Name: ${event.name}")
        println("   Owner: ${event.ownerId}")
        println("   Language: ${event.language}")
        println("   Version: ${event.version}")

        // TODO: Implementar lógica de negocio
        // Por ejemplo:
        // - Validar sintaxis del snippet
        // - Ejecutar análisis estático
        // - Formatear el código
        // - Guardar metadatos en tu DB
    }

    private fun handleUpdate(event: SnippetEvent) {
        println("✏️ Snippet updated:")
        println("   ID: ${event.snippetId}")
        println("   Name: ${event.name}")

        event.content?.let { content ->
            println("   Content updated (${content.length} chars)")
            // TODO: Procesar el nuevo contenido
            // - Re-validar sintaxis
            // - Re-ejecutar análisis
            // - Actualizar índices de búsqueda
        }
    }

    private fun handleDelete(event: SnippetEvent) {
        println("🗑️ Snippet deleted:")
        println("   ID: ${event.snippetId}")
        println("   Name: ${event.name}")

        // TODO: Limpiar recursos
        // - Eliminar caché
        // - Eliminar metadatos
        // - Limpiar índices
    }
}
