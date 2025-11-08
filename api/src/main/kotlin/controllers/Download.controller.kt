package controllers

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
import services.FormatterService
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/download")
class DownloadController(
    private val assetServiceClient: AssetServiceClient,
    private val formatterService: FormatterService,
) {
    @GetMapping("/formatted")
    fun downloadFormatted(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
        @RequestParam("userId") userId: String,
    ): ResponseEntity<Resource> {
        val content = assetServiceClient.getAsset(container, key)

        val inputStream = ByteArrayInputStream(content.toByteArray(StandardCharsets.UTF_8))
        val formattedContent = formatterService.format(inputStream, version, userId)

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
