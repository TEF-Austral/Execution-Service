package api.controllers

import api.dtos.CreatePermissionRequestDTO
import api.dtos.CreateSnippetRequest
import api.dtos.ShareRequestDTO
import api.dtos.UpdateSnippetRequestDTO
import api.entities.PermissionType
import api.entities.Snippet
import api.repositories.SnippetRepository
import api.services.AuthorizationServiceClient
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/snippets")
class SnippetController(
    private val snippetRepository: SnippetRepository,
    private val authorizationClient: AuthorizationServiceClient,
    // Inyecta aquí tus servicios para S3/Bucket, ejecución, etc.
) {

    /**
     * Función central de ABAC para este controlador.
     * Verifica si el usuario es dueño O tiene permiso explícito.
     */
    private fun isAllowed(
        subjectId: String,
        snippetId: Long,
        action: PermissionType,
    ): Boolean {
        // 1. Cargar snippet de la BD local (considerando soft deletes)
        val snippet =
            snippetRepository.findById(snippetId).orElse(null)
                ?: return false // Snippet no existe

        // 2. Verificar si está borrado (soft delete)
        if (snippet.deletedAt != null) {
            return false
        }

        // 3. Verificar si es el dueño
        if (snippet.ownerId == subjectId) {
            return true // El dueño tiene todos los permisos
        }

        // 4. Si no es dueño, consultar al servicio de autorización
        return authorizationClient.checkPermission(subjectId, snippetId, action)
    }

    private fun getSubjectId(auth: Authentication): String = (auth.principal as Jwt).subject

    // --- Endpoints ---

    /**
     * ACCIÓN: Crear
     * POLÍTICA: Cualquier usuario autenticado puede crear.
     */
    @PostMapping
    fun createSnippet(
        @RequestBody request: CreateSnippetRequest,
        auth: Authentication,
    ): ResponseEntity<Snippet> {
        val subjectId = getSubjectId(auth)

        // 1. Guarda el contenido en tu bucket, obtén el ID del bucket
        val snippetInBucket = saveToBucket(request.content) // Tu lógica aquí

        // 2. Crea la entidad Snippet en la DB local
        val newSnippet =
            Snippet(
                name = request.name,
                snippetInBucket = snippetInBucket,
                language = request.language,
                version = request.version,
                ownerId = subjectId,
                deletedAt = null,
                tests = mutableListOf(),
            )
        val savedSnippet = snippetRepository.save(newSnippet)

        // 3. ¡IMPORTANTE! Otorgar TODOS los permisos al creador/dueño
        PermissionType.entries.forEach { permission ->
            authorizationClient.grantPermission(
                CreatePermissionRequestDTO(
                    userId = subjectId,
                    snippetId = savedSnippet.id,
                    permission = permission,
                ),
            )
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedSnippet)
    }

    /**
     * ACCIÓN: Leer
     * POLÍTICA: Requiere permiso 'READ' (o ser dueño)
     */
    @GetMapping("/{id}")
    fun getSnippet(
        @PathVariable id: Long,
        auth: Authentication,
    ): ResponseEntity<Snippet> {
        val subjectId = getSubjectId(auth)

        if (!isAllowed(subjectId, id, PermissionType.READ)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val snippet =
            snippetRepository.findById(id).orElse(null)
                ?: return ResponseEntity.notFound().build()

        // Verificar soft delete
        if (snippet.deletedAt != null) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(snippet)
    }

    /**
     * ACCIÓN: Editar
     * POLÍTICA: Requiere permiso 'EDIT' (o ser dueño)
     */
    @PutMapping("/{id}")
    fun updateSnippet(
        @PathVariable id: Long,
        @RequestBody request: UpdateSnippetRequestDTO,
        auth: Authentication,
    ): ResponseEntity<Snippet> {
        val subjectId = getSubjectId(auth)

        if (!isAllowed(subjectId, id, PermissionType.EDIT)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val snippet =
            snippetRepository.findById(id).orElse(null)
                ?: return ResponseEntity.notFound().build()

        if (snippet.deletedAt != null) {
            return ResponseEntity.notFound().build()
        }

        // Actualiza el contenido en el bucket si es necesario
        request.content?.let {
            updateInBucket(snippet.snippetInBucket, it)
        }

        // Crea nuevo snippet con campos actualizados
        val updatedSnippet =
            snippet.copy(
                name = request.name ?: snippet.name,
                version = request.version ?: snippet.version,
                language = request.language ?: snippet.language,
            )

        return ResponseEntity.ok(snippetRepository.save(updatedSnippet))
    }

    /**
     * ACCIÓN: Borrar (Soft Delete)
     * POLÍTICA: Requiere permiso 'DELETE' (o ser dueño)
     */
    @DeleteMapping("/{id}")
    fun deleteSnippet(
        @PathVariable id: Long,
        auth: Authentication,
    ): ResponseEntity<Void> {
        val subjectId = getSubjectId(auth)

        if (!isAllowed(subjectId, id, PermissionType.DELETE)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val snippet =
            snippetRepository.findById(id).orElse(null)
                ?: return ResponseEntity.notFound().build()

        // Soft delete
        val deletedSnippet = snippet.copy(deletedAt = LocalDateTime.now())
        snippetRepository.save(deletedSnippet)

        // Opcional: Llama a authorization-service para borrar los permisos asociados
        // authorizationClient.revokeAllPermissionsForSnippet(id)

        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/run-tests")
    fun runTests(
        @PathVariable id: Long,
        auth: Authentication,
    ): ResponseEntity<String> {
        val subjectId = getSubjectId(auth)

        if (!isAllowed(subjectId, id, PermissionType.RUN_TESTS)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val snippet =
            snippetRepository.findById(id).orElse(null)
                ?: return ResponseEntity.notFound().build()

        if (snippet.deletedAt != null) {
            return ResponseEntity.notFound().build()
        }

        // Lógica para ejecutar tests
        val results = "Tests para Snippet ${snippet.name} ejecutados OK."
        return ResponseEntity.ok(results)
    }

    /**
     * ACCIÓN: Compartir (Conceder permisos a otros)
     * POLÍTICA: Requiere permiso 'SHARE' (o ser dueño)
     */
    @PostMapping("/{id}/share")
    fun shareSnippet(
        @PathVariable id: Long,
        @RequestBody shareRequest: ShareRequestDTO,
        auth: Authentication,
    ): ResponseEntity<Any> {
        val subjectId = getSubjectId(auth)

        if (!isAllowed(subjectId, id, PermissionType.SHARE)) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("No tienes permiso para 'compartir' este snippet.")
        }

        val success =
            authorizationClient.grantPermission(
                CreatePermissionRequestDTO(
                    userId = shareRequest.userIdToShareWith,
                    snippetId = id,
                    permission = shareRequest.permissionToGrant,
                ),
            )

        return if (success) {
            ResponseEntity.status(HttpStatus.CREATED).build()
        } else {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("No se pudo conceder el permiso.")
        }
    }

    // Métodos helper (implementa según tu infraestructura)
    private fun saveToBucket(content: String): Long {
        // TODO: Implementar lógica para guardar en bucket
        return 0L
    }

    private fun updateInBucket(
        bucketId: Long,
        content: String,
    ) {
        // TODO: Implementar lógica para actualizar en bucket
    }
}
