package controllers

import Language
import component.AssetServiceClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import services.LanguagesFormatterService
import services.FormatterConfigService
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/format")
class FormatterController(
    private val languagesFormatterService: LanguagesFormatterService,
    private val formatterConfigService: FormatterConfigService,
    private val assetServiceClient: AssetServiceClient,
) {
    private val log = org.slf4j.LoggerFactory.getLogger(FormatterController::class.java)

    @PostMapping
    fun formatCode(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
        @RequestParam("userId") userId: String,
        @RequestParam("language") language: Language,
    ): ResponseEntity<String> {
        log.info(
            "POST /format - Formatting code for user $userId, version $version, language $language",
        )
        val assetContent = assetServiceClient.getAsset(container, key)
        val rules = formatterConfigService.getConfig(userId)
        val config = formatterConfigService.rulesToConfigDTO(rules)
        val inputStream = ByteArrayInputStream(assetContent.toByteArray(StandardCharsets.UTF_8))
        val formattedContent =
            languagesFormatterService.format(
                inputStream,
                version,
                config,
                language,
            )
        assetServiceClient.createOrUpdateAsset(container, key, formattedContent)
        log.warn("POST /format - Code formatted and saved successfully")
        return ResponseEntity.ok(formattedContent)
    }

    @PostMapping("/preview")
    fun previewFormat(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
        @RequestParam("userId") userId: String,
        @RequestParam("language") language: Language,
    ): ResponseEntity<String> {
        log.info(
            "POST /format/preview - Previewing format for user $userId, version $version, language $language",
        )
        val rules = formatterConfigService.getConfig(userId)
        val config = formatterConfigService.rulesToConfigDTO(rules)
        val assetContent = assetServiceClient.getAsset(container, key)
        val inputStream = ByteArrayInputStream(assetContent.toByteArray(StandardCharsets.UTF_8))
        log.debug("User Id: $userId")
        val formattedContent =
            languagesFormatterService.format(
                inputStream,
                version,
                config,
                language,
            )
        log.warn("POST /format/preview - Format preview generated successfully")
        return ResponseEntity.ok(formattedContent)
    }
}
