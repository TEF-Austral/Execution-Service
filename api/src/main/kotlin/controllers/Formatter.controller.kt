package controllers

import component.AssetServiceClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
) {

    @PostMapping
    fun formatCode(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
        @RequestParam("userId") userId: String,
    ): ResponseEntity<String> {
        val assetContent = assetServiceClient.getAsset(container, key)
        val rules = formatterConfigService.getConfig(userId)
        val config = formatterConfigService.rulesToConfigDTO(rules)
        val inputStream = ByteArrayInputStream(assetContent.toByteArray(StandardCharsets.UTF_8))
        println("User Id: $userId")
        val formattedContent = formatterService.format(inputStream, version, config)
        assetServiceClient.createOrUpdateAsset(container, key, formattedContent)
        return ResponseEntity.ok(formattedContent)
    }

    @PostMapping("/preview")
    fun previewFormat(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
        @RequestParam("userId") userId: String,
    ): ResponseEntity<String> {
        val rules = formatterConfigService.getConfig(userId)
        val config = formatterConfigService.rulesToConfigDTO(rules)
        val assetContent = assetServiceClient.getAsset(container, key)
        val inputStream = ByteArrayInputStream(assetContent.toByteArray(StandardCharsets.UTF_8))
        println("User Id: $userId")
        val formattedContent = formatterService.format(inputStream, version, config)
        return ResponseEntity.ok(formattedContent)
    }
}
