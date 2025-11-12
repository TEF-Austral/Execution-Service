package controllers

import Language
import component.AssetServiceClient
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
import services.LanguagesFormatterService
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/download")
class DownloadController(
    private val assetServiceClient: AssetServiceClient,
    private val languagesFormatterService: LanguagesFormatterService,
    private val formatterConfigService: FormatterConfigService,
) {
    private val log = org.slf4j.LoggerFactory.getLogger(DownloadController::class.java)

    @GetMapping("/formatted")
    fun downloadFormatted(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
        @RequestParam("userId") userId: String,
        @RequestParam("language") language: Language,
    ): ResponseEntity<Resource> {
        log.info(
            "GET /download/formatted - Downloading formatted snippet for user $userId, language $language",
        )
        val content = assetServiceClient.getAsset(container, key)
        val rules = formatterConfigService.getConfig(userId)
        val config = formatterConfigService.rulesToConfigDTO(rules)
        val inputStream = ByteArrayInputStream(content.toByteArray(StandardCharsets.UTF_8))
        val formattedContent =
            languagesFormatterService.format(
                inputStream,
                version,
                config,
                language,
            )
        val resource = ByteArrayResource(formattedContent.toByteArray(StandardCharsets.UTF_8))

        log.warn("GET /download/formatted - Formatted snippet ready for download")
        return ResponseEntity
            .ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"snippet-formatted.ps\"",
            ).contentType(MediaType.TEXT_PLAIN)
            .body(resource)
    }
}
