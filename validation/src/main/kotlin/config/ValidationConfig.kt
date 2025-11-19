package config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import rules.AstValidator
import rules.rules.NoReadEnvRule
import rules.validation.CompositeValidator

@Configuration
class ValidationConfig {

    @Bean
    fun printScriptValidator(): AstValidator {
        val rules =
            listOf(
                NoReadEnvRule(),
            )
        return CompositeValidator(rules)
    }
}
