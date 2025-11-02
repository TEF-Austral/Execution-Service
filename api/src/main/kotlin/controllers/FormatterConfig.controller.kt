package controllers

import component.AuthenticatedUserProvider
import dtos.FormatterRuleDTO
import dtos.UpdateFormatterConfigRequestDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import services.FormatterConfigService

@RestController
@RequestMapping("/format/rules")
class FormatterConfigController(
    private val formatterConfigService: FormatterConfigService,
    private val authenticatedUserProvider: AuthenticatedUserProvider,
) {

    @GetMapping
    fun getConfig(): ResponseEntity<List<FormatterRuleDTO>> {
        val userId = authenticatedUserProvider.getCurrentUserId()
        val rules = formatterConfigService.getConfig(userId)
        return ResponseEntity.ok(rules)
    }

    @PutMapping
    fun updateConfig(
        @RequestBody request: UpdateFormatterConfigRequestDTO,
    ): ResponseEntity<List<FormatterRuleDTO>> {
        val userId = authenticatedUserProvider.getCurrentUserId()
        val updatedRules = formatterConfigService.updateConfig(userId, request.rules)
        return ResponseEntity.ok(updatedRules)
    }
}
