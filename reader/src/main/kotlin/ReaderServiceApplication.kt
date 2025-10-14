package reader

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReaderServiceApplication

fun main(args: Array<String>) {
    runApplication<ReaderServiceApplication>(*args)
}
