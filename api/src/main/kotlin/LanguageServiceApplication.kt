package api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
    scanBasePackages = [
        "api", "assets", "services",
        "repositories", "helpers", "controllers",
        "dtos", "entities", "security",
        "events", "component", "config",
        "consumers", "producers", "requests",
        "result", "consumers.handlers", "handlers",
        "mappers", "factories",
    ],
)
@EnableJpaRepositories(basePackages = ["repositories"])
@EntityScan(basePackages = ["entities"])
class LanguageServiceApplication

fun main(args: Array<String>) {
    runApplication<LanguageServiceApplication>(*args)
}
