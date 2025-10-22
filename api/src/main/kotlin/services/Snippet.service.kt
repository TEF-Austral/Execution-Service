package api.services

import api.entities.Snippet
import api.repositories.SnippetRepository // Importa tu repositorio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional // Para asegurar consistencia en BD

@Service // Marca esta clase para que Spring la gestione
class SnippetService(
    private val snippetRepository: SnippetRepository,
    // Aquí podrías inyectar otros servicios/clientes (ej. para S3/GCS, para llamar a otros microservicios)
    // private val s3Client: AmazonS3,
    // @Value("\${aws.s3.bucket}") private val bucketName: String
) {

    fun findById(id: Long): Snippet? {
        // Usa el repositorio JPA (la implementación la da Spring)
        return snippetRepository.findById(id).orElse(null)
    }

    @Transactional // Buena práctica para operaciones de escritura
    fun save(snippet: Snippet): Snippet {
        // Usa el repositorio JPA
        return snippetRepository.save(snippet)
    }

    @Transactional
    fun deleteById(id: Long) {
        // Podrías añadir lógica aquí para borrar también de S3/GCS antes
        // val snippet = findById(id)
        // if (snippet != null) {
        //     s3Client.deleteObject(bucketName, snippet.contentRef)
        // }

        // Usa el repositorio JPA
        snippetRepository.deleteById(id)

        // NOTA: También necesitarías llamar al authorization-service
        // para borrar los permisos asociados a este snippet.
    }

    fun runTests(snippetId: Long): String {
        val snippet =
            findById(snippetId)
                ?: return "Error: Snippet no encontrado"

        // --- Lógica para correr tests ---
        // 1. Leer el contenido desde S3/GCS usando snippet.contentRef
        //    val content = s3Client.getObjectAsString(bucketName, snippet.contentRef)
        // 2. Llamar al servicio de interpretación/ejecución (¿es este mismo servicio?)
        //    o enviar a una cola, etc.
        // 3. Devolver los resultados

        return "Simulación: Tests para Snippet $snippetId ejecutados OK."
    }
}
