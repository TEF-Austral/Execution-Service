package controllers

import dtos.AnalyzerRuleDTO
import dtos.UpdateAnalyzerConfigRequestDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import services.AnalyzerConfigService

@RestController
@RequestMapping("/analyze/rules")
class AnalyzerConfigController(
    private val analyzerConfigService: AnalyzerConfigService,
) {

    @GetMapping
    fun getConfig(
        @RequestParam("userId") userId: String,
    ): ResponseEntity<List<AnalyzerRuleDTO>> {
        val rules = analyzerConfigService.getConfig(userId)
        return ResponseEntity.ok(rules)
    }

    @PutMapping
    fun updateConfig(
        @RequestParam("userId") userId: String,
        @RequestBody request: UpdateAnalyzerConfigRequestDTO,
    ): ResponseEntity<List<AnalyzerRuleDTO>> {
        val updatedRules = analyzerConfigService.updateConfig(userId, request.rules)
        return ResponseEntity.ok(updatedRules)
    }
}
