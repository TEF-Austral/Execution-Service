package controllers

import dtos.FormatConfigDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import services.FormatterConfigService

@RestController
@RequestMapping("/format/config")
class FormatterConfigController(
    private val formatterConfigService: FormatterConfigService,
) {

    @GetMapping
    fun getConfig(
        @RequestParam("userId") userId: String,
    ): ResponseEntity<FormatConfigDTO> {
        val config = formatterConfigService.getConfig(userId)
        return ResponseEntity.ok(config)
    }

    @PutMapping
    fun updateConfig(
        @RequestParam("userId") userId: String,
        @RequestBody config: FormatConfigDTO,
    ): ResponseEntity<FormatConfigDTO> {
        val updatedConfig = formatterConfigService.updateConfig(userId, config)
        return ResponseEntity.ok(updatedConfig)
    }
}
