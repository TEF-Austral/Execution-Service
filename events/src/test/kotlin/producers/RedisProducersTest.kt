package producers

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.RedisTemplate

class RedisProducersTest {

    private lateinit var redisTemplate: RedisTemplate<String, String>

    @BeforeEach
    fun setup() {
        redisTemplate = mockk(relaxed = true)
    }

    @Test
    fun `AnalyzerRulesUpdatedProducer should be instantiated`() {
        val producer = AnalyzerRulesUpdatedProducer("test-stream", redisTemplate)
        assertNotNull(producer)
    }

    @Test
    fun `FormattingResultProducer should be instantiated`() {
        val producer = FormattingResultProducer("test-stream", redisTemplate)
        assertNotNull(producer)
    }

    @Test
    fun `FormattingRulesUpdatedProducer should be instantiated`() {
        val producer = FormattingRulesUpdatedProducer("test-stream", redisTemplate)
        assertNotNull(producer)
    }

    @Test
    fun `LintingResultProducer should be instantiated`() {
        val producer = LintingResultProducer("test-stream", redisTemplate)
        assertNotNull(producer)
    }

    @Test
    fun `TestingResultProducer should be instantiated`() {
        val producer = TestingResultProducer("test-stream", redisTemplate)
        assertNotNull(producer)
    }
}
