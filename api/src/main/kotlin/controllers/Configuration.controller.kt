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
    private val log = org.slf4j.LoggerFactory.getLogger(ConfigurationController::class.java)

    @GetMapping("/analyze")
    fun getAnalyzerConfig(): ResponseEntity<List<AnalyzerRuleDTO>> {
        val userId = authenticatedUserProvider.getCurrentUserId()
        log.info("GET /config/analyze - Fetching analyzer config for user $userId")
        println("User Id: $userId")
        val rules = analyzerConfigService.getConfig(userId)
        log.warn("GET /config/analyze - Retrieved ${rules.size} analyzer rules")
        return ResponseEntity.ok(rules)
    }

    @PutMapping("/update/analyze")
    fun updateAnalyzerConfig(
        @RequestBody request: UpdateAnalyzerConfigRequestDTO,
    ): ResponseEntity<List<AnalyzerRuleDTO>> {
        val userId = authenticatedUserProvider.getCurrentUserId()
        log.info("PUT /config/update/analyze - Updating analyzer config for user $userId")
        val updatedRules = analyzerConfigService.updateConfig(userId, request.rules)
        log.warn("PUT /config/update/analyze - Analyzer config updated, ${updatedRules.size} rules")
        return ResponseEntity.ok(updatedRules)
    }

    @GetMapping("/format")
    fun getFormatterConfig(): ResponseEntity<List<FormatterRuleDTO>> {
        val userId = authenticatedUserProvider.getCurrentUserId()
        log.info("GET /config/format - Fetching formatter config for user $userId")
        val rules = formatterConfigService.getConfig(userId)
        log.warn("GET /config/format - Retrieved ${rules.size} formatter rules")
        return ResponseEntity.ok(rules)
    }

    @PutMapping("/update/format")
    fun updateFormatterConfig(
        @RequestBody request: UpdateFormatterConfigRequestDTO,
    ): ResponseEntity<List<FormatterRuleDTO>> {
        val userId = authenticatedUserProvider.getCurrentUserId()
        log.info("PUT /config/update/format - Updating formatter config for user $userId")
        val updatedRules = formatterConfigService.updateConfig(userId, request.rules)
        log.warn("PUT /config/update/format - Formatter config updated, ${updatedRules.size} rules")
        return ResponseEntity.ok(updatedRules)
    }
}
