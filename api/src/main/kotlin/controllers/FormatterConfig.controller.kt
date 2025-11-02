package controllers

import dtos.RuleDTO
import dtos.UpdateConfigRequestDTO
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
    ): ResponseEntity<List<RuleDTO>> {
        val rules = formatterConfigService.getConfig(userId)
        return ResponseEntity.ok(rules)
    }

    @PutMapping
    fun updateConfig(
        @RequestParam("userId") userId: String,
        @RequestBody request: UpdateConfigRequestDTO,
    ): ResponseEntity<List<RuleDTO>> {
        val updatedRules = formatterConfigService.updateConfig(userId, request.rules)
        return ResponseEntity.ok(updatedRules)
    }
}
