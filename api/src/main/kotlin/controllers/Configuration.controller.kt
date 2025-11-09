package controllers

import dtos.AnalyzerRuleDTO
import dtos.FormatterRuleDTO
import dtos.UpdateAnalyzerConfigRequestDTO
import dtos.UpdateFormatterConfigRequestDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import security.AuthenticatedUserProvider
import services.AnalyzerConfigService
import services.FormatterConfigService

@RestController
@RequestMapping("/config")
class ConfigurationController(
    private val analyzerConfigService: AnalyzerConfigService,
    private val formatterConfigService: FormatterConfigService,
    private val authenticatedUserProvider: AuthenticatedUserProvider,
) {

    @GetMapping("/analyze")
    fun getAnalyzerConfig(): ResponseEntity<List<AnalyzerRuleDTO>> {
        val userId = authenticatedUserProvider.getCurrentUserId()
        println("User Id: $userId")
        val rules = analyzerConfigService.getConfig(userId)
        return ResponseEntity.ok(rules)
    }

    @PutMapping("/update/analyze")
    fun updateAnalyzerConfig(
        @RequestBody request: UpdateAnalyzerConfigRequestDTO,
    ): ResponseEntity<List<AnalyzerRuleDTO>> {
        val userId = authenticatedUserProvider.getCurrentUserId()
        val updatedRules = analyzerConfigService.updateConfig(userId, request.rules)
        return ResponseEntity.ok(updatedRules)
    }

    @GetMapping("/format")
    fun getFormatterConfig(): ResponseEntity<List<FormatterRuleDTO>> {
        val userId = authenticatedUserProvider.getCurrentUserId()
        println("User Id: $userId")
        val rules = formatterConfigService.getConfig(userId)
        return ResponseEntity.ok(rules)
    }

    @PutMapping("/update/format")
    fun updateFormatterConfig(
        @RequestBody request: UpdateFormatterConfigRequestDTO,
    ): ResponseEntity<List<FormatterRuleDTO>> {
        val userId = authenticatedUserProvider.getCurrentUserId()
        val updatedRules = formatterConfigService.updateConfig(userId, request.rules)
        return ResponseEntity.ok(updatedRules)
    }
}
