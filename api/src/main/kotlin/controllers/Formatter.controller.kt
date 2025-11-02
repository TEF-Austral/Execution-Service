package controllers

import component.AssetServiceClient
import dtos.FormatConfigDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import security.AuthenticatedUserProvider
import services.FormatterConfigService
import services.FormatterService
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/format")
class FormatterController(
    private val formatterService: FormatterService,
    private val formatterConfigService: FormatterConfigService,
    private val assetServiceClient: AssetServiceClient,
    private val authenticatedUserProvider: AuthenticatedUserProvider,
) {

    @PostMapping
    fun formatCode(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
        @RequestBody config: FormatConfigDTO,
    ): ResponseEntity<String> {
        val assetContent = assetServiceClient.getAsset(container, key)
        val inputStream = ByteArrayInputStream(assetContent.toByteArray(StandardCharsets.UTF_8))

        val formattedContent = formatterService.format(inputStream, version, config)

        assetServiceClient.createOrUpdateAsset(container, key, formattedContent)

        return ResponseEntity.ok(formattedContent)
    }

    @PostMapping("/preview")
    fun previewFormat(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
    ): ResponseEntity<String> {
        val userId = authenticatedUserProvider.getCurrentUserId()
        val rules = formatterConfigService.getConfig(userId)
        val config = formatterConfigService.rulesToConfigDTO(rules)

        val assetContent = assetServiceClient.getAsset(container, key)
        val inputStream = ByteArrayInputStream(assetContent.toByteArray(StandardCharsets.UTF_8))
        val formattedContent = formatterService.format(inputStream, version, config)

        return ResponseEntity.ok(formattedContent)
    }
}
