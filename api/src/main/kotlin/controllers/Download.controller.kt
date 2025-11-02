package controllers

import component.AssetServiceClient
import component.AuthenticatedUserProvider
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import services.FormatterConfigService
import services.FormatterService
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/download")
class DownloadController(
    private val assetServiceClient: AssetServiceClient,
    private val formatterService: FormatterService,
    private val formatterConfigService: FormatterConfigService,
    private val authenticatedUserProvider: AuthenticatedUserProvider,
) {
    @GetMapping("/formatted")
    fun downloadFormatted(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
    ): ResponseEntity<Resource> {
        val userId = authenticatedUserProvider.getCurrentUserId()
        val content = assetServiceClient.getAsset(container, key)
        val rules = formatterConfigService.getConfig(userId)
        val config = formatterConfigService.rulesToConfigDTO(rules)

        val inputStream = ByteArrayInputStream(content.toByteArray(StandardCharsets.UTF_8))
        val formattedContent = formatterService.format(inputStream, version, config)

        val resource = ByteArrayResource(formattedContent.toByteArray(StandardCharsets.UTF_8))

        return ResponseEntity
            .ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"snippet-formatted.ps\"",
            ).contentType(MediaType.TEXT_PLAIN)
            .body(resource)
    }
}
