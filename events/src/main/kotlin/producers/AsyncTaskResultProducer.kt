package producers

import TestingResultEvent
import org.austral.ingsis.redis.RedisStreamProducer
import result.FormattingResultEvent
import result.LintingResultEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AsyncTaskResultProducer(
    @param:Autowired private val producer: RedisStreamProducer,
) {

    @Value("\${redis.stream.formatting.result.key}")
    private lateinit var formattingResultKey: String

    @Value("\${redis.stream.linting.result.key}")
    private lateinit var lintingResultKey: String

    @Value("\${redis.stream.testing.result.key}")
    private lateinit var testingResultKey: String

    fun publishFormattingResult(result: FormattingResultEvent) {
        producer.emit(result)
        println("📤 [PrintScript Service] Published formatting RESULT: ${result.requestId}")
    }

    fun publishLintingResult(result: LintingResultEvent) {
        producer.emit(result)
        println("📤 [PrintScript Service] Published linting RESULT: ${result.requestId}")
    }

    fun publishTestingResult(result: TestingResultEvent) {
        producer.emit(result)
        println("📤 [PrintScript Service] Published testing RESULT: ${result.requestId}")
    }
}
