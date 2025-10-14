package interpreter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExecutionServiceApplication

fun main(args: Array<String>) {
    runApplication<ExecutionServiceApplication>(*args)
}
