package producers

import dtos.responses.FormattingResultEvent
import dtos.responses.LintingResultEvent
import dtos.responses.TestingResultEvent
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.RedisTemplate

class ResultProducersTest {

    private lateinit var redisTemplate: RedisTemplate<String, String>

    @BeforeEach
    fun setup() {
        redisTemplate = mockk(relaxed = true)
    }

    @Test
    fun `FormattingResultProducer should be instantiated and emit event`() {
        val producer = FormattingResultProducer("test-stream", redisTemplate)
        assertNotNull(producer)

        val event =
            FormattingResultEvent(
                requestId = "req-123",
                success = true,
                formattedContent = "formatted",
                error = null,
                snippetId = 1L,
            )

        producer.emit(event)

        verify { redisTemplate.opsForStream<String, Any>() }
    }

    @Test
    fun `LintingResultProducer should be instantiated and emit event`() {
        val producer = LintingResultProducer("test-stream", redisTemplate)
        assertNotNull(producer)

        val event =
            LintingResultEvent(
                requestId = "req-456",
                isValid = true,
                violations = emptyList(),
                snippetId = 2L,
            )

        producer.emit(event)

        verify { redisTemplate.opsForStream<String, Any>() }
    }

    @Test
    fun `TestingResultProducer should be instantiated and emit event`() {
        val producer = TestingResultProducer("test-stream", redisTemplate)
        assertNotNull(producer)

        val event =
            TestingResultEvent(
                requestId = "req-789",
                testId = 1L,
                snippetId = 3L,
                passed = true,
                outputs = listOf("42"),
                expectedOutputs = listOf("42"),
                errors = emptyList(),
            )

        producer.emit(event)

        verify { redisTemplate.opsForStream<String, Any>() }
    }

    @Test
    fun `FormattingRulesUpdatedProducer should emit rules updated event`() {
        val producer = FormattingRulesUpdatedProducer("test-stream", redisTemplate)
        assertNotNull(producer)

        val event = events.FormattingRulesUpdatedEvent(userId = "user-123")

        producer.emit(event)

        verify { redisTemplate.opsForStream<String, Any>() }
    }

    @Test
    fun `AnalyzerRulesUpdatedProducer should emit rules updated event`() {
        val producer = AnalyzerRulesUpdatedProducer("test-stream", redisTemplate)
        assertNotNull(producer)

        val event = events.AnalyzerRulesUpdatedEvent(userId = "user-456")

        producer.emit(event)

        verify { redisTemplate.opsForStream<String, Any>() }
    }

    @Test
    fun `FormattingResultProducer should handle error event`() {
        val producer = FormattingResultProducer("test-stream", redisTemplate)

        val event =
            FormattingResultEvent(
                requestId = "req-error",
                success = false,
                formattedContent = null,
                error = "Formatting failed",
                snippetId = 1L,
            )

        producer.emit(event)

        verify { redisTemplate.opsForStream<String, Any>() }
    }

    @Test
    fun `LintingResultProducer should handle invalid result with violations`() {
        val producer = LintingResultProducer("test-stream", redisTemplate)

        val event =
            LintingResultEvent(
                requestId = "req-violations",
                isValid = false,
                violations =
                    listOf(
                        dtos.responses.ViolationDTO("Error 1", 1, 5),
                        dtos.responses.ViolationDTO("Error 2", 2, 10),
                    ),
                snippetId = 2L,
            )

        producer.emit(event)

        verify { redisTemplate.opsForStream<String, Any>() }
    }

    @Test
    fun `TestingResultProducer should handle failed tests`() {
        val producer = TestingResultProducer("test-stream", redisTemplate)

        val event =
            TestingResultEvent(
                requestId = "req-failed",
                testId = 1L,
                snippetId = 3L,
                passed = false,
                outputs = listOf("actual"),
                expectedOutputs = listOf("expected"),
                errors = listOf("Mismatch"),
            )

        producer.emit(event)

        verify { redisTemplate.opsForStream<String, Any>() }
    }
}
