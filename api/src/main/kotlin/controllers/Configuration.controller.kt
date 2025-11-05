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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import services.AnalyzerConfigService
import services.FormatterConfigService

@RestController
@RequestMapping("/config")
class ConfigurationController(
    private val analyzerConfigService: AnalyzerConfigService,
    private val formatterConfigService: FormatterConfigService,
) {

    @GetMapping("/analyze")
    fun getAnalyzerConfig(
        @RequestParam("userId") userId: String,
    ): ResponseEntity<List<AnalyzerRuleDTO>> {
        val rules = analyzerConfigService.getConfig(userId)
        return ResponseEntity.ok(rules)
    }

    @PutMapping("/update/analyze")
    fun updateAnalyzerConfig(
        @RequestParam("userId") userId: String,
        @RequestBody request: UpdateAnalyzerConfigRequestDTO,
    ): ResponseEntity<List<AnalyzerRuleDTO>> {
        val updatedRules = analyzerConfigService.updateConfig(userId, request.rules)
        return ResponseEntity.ok(updatedRules)
    }

    @GetMapping("/format")
    fun getFormatterConfig(
        @RequestParam("userId") userId: String,
    ): ResponseEntity<List<FormatterRuleDTO>> {
        val rules = formatterConfigService.getConfig(userId)
        return ResponseEntity.ok(rules)
    }

    @PutMapping("/update/format")
    fun updateFormatterConfig(
        @RequestParam("userId") userId: String,
        @RequestBody request: UpdateFormatterConfigRequestDTO,
    ): ResponseEntity<List<FormatterRuleDTO>> {
        val updatedRules = formatterConfigService.updateConfig(userId, request.rules)
        return ResponseEntity.ok(updatedRules)
    }
}
