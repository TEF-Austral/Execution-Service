package api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
    scanBasePackages = [
        "api", "component", "services",
        "repositories", "helpers", "config",
        "dtos", "entities", "security",
    ],
)
@EnableJpaRepositories(basePackages = ["repositories"])
@EntityScan(basePackages = ["entities"])
class PrintScriptServiceApplication {
    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()
}

fun main(args: Array<String>) {
    runApplication<PrintScriptServiceApplication>(*args)
}
