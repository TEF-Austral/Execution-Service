package api.services

import api.dtos.AuthorizationCheckResponseDTO
import api.dtos.CreatePermissionRequestDTO
import api.entities.PermissionType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class AuthorizationServiceClient(
    private val restTemplate: RestTemplate,
    @Value("\${services.authorization.url}") private val authorizationServiceUrl: String,
) {
    private val logger = LoggerFactory.getLogger(AuthorizationServiceClient::class.java)

    /**
     * Llama al endpoint /permissions/check del authorization-service.
     * Devuelve true si el permiso existe, false si no existe o si hay error.
     */
    fun checkPermission(
        userId: String,
        snippetId: Long,
        action: PermissionType,
    ): Boolean {
        val checkUrl =
            UriComponentsBuilder
                .fromHttpUrl(authorizationServiceUrl)
                .path("/permissions/check")
                .queryParam("user_id", userId)
                .queryParam("snippet_id", snippetId)
                .queryParam("action", action)
                .toUriString()

        return try {
            val response =
                restTemplate.getForObject(
                    checkUrl,
                    AuthorizationCheckResponseDTO::class.java,
                )
            response?.allowed ?: false
        } catch (e: HttpClientErrorException) {
            logger.error(
                "Error calling authorization service at $checkUrl: ${e.statusCode} ${e.responseBodyAsString}",
                e,
            )
            false // Denegar por seguridad en caso de error
        } catch (e: Exception) {
            logger.error(
                "Network or unexpected error calling authorization service at $checkUrl",
                e,
            )
            false // Denegar por seguridad
        }
    }

    /**
     * Llama al (futuro) endpoint POST /permissions del authorization-service
     * para crear un nuevo permiso. Devuelve true si fue exitoso.
     */
    fun grantPermission(request: CreatePermissionRequestDTO): Boolean {
        val grantUrl =
            UriComponentsBuilder
                .fromHttpUrl(authorizationServiceUrl)
                .path("/permissions")
                .toUriString()

        return try {
            val response: ResponseEntity<Void> =
                restTemplate.postForEntity(
                    grantUrl,
                    request,
                    Void::class.java,
                )
            response.statusCode == HttpStatus.CREATED
        } catch (e: HttpClientErrorException) {
            if (e.statusCode == HttpStatus.CONFLICT) {
                logger.warn("Permission already exists: $request")
                return true // Si ya existe, consideramos la operación exitosa
            }
            logger.error(
                "Error calling authorization service POST $grantUrl: ${e.statusCode} ${e.responseBodyAsString}",
                e,
            )
            false
        } catch (e: Exception) {
            logger.error(
                "Network or unexpected error calling authorization service POST $grantUrl",
                e,
            )
            false
        }
    }

    // Podrías añadir un método 'revokePermission' similar usando DELETE
}
