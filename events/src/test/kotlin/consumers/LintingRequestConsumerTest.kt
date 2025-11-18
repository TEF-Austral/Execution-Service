package consumers

import consumers.handlers.ILintingRequestHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import requests.LintingRequestEvent
import java.lang.reflect.Method
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class LintingRequestConsumerTest {

    private lateinit var handler: ILintingRequestHandler
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var consumer: LintingRequestConsumer
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var onMessageMethod: Method
    private lateinit var optionsMethod: Method

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        handler = mockk(relaxed = true)
        redisTemplate = mockk(relaxed = true)
        consumer =
            LintingRequestConsumer(
                streamKey = "test-linting-stream",
                consumerGroup = "test-group",
                redis = redisTemplate,
                handler = handler,
            )

        onMessageMethod =
            LintingRequestConsumer::class.java
                .getDeclaredMethod("onMessage", ObjectRecord::class.java)
        onMessageMethod.isAccessible = true

        optionsMethod =
            LintingRequestConsumer::class.java
                .getDeclaredMethod("options")
        optionsMethod.isAccessible = true
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onMessage should call handler with linting request event`() {
        val event =
            LintingRequestEvent(
                requestId = "req-456",
                snippetId = 2L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
                userId = "user-123",
                languageId = "PRINTSCRIPT",
            )
        val record: ObjectRecord<String, LintingRequestEvent> =
            mockk {
                every { value } returns event
            }

        onMessageMethod.invoke(consumer, record)
        Thread.sleep(100)

        verify(exactly = 1) { handler.handle(event) }
    }

    @Test
    fun `options should return correct configuration`() {
        @Suppress("UNCHECKED_CAST")
        val options =
            optionsMethod.invoke(consumer) as StreamReceiver.StreamReceiverOptions<
                String,
                ObjectRecord<String, LintingRequestEvent>,
            >

        assertNotNull(options)
        assertEquals(Duration.ofMillis(10000), options.pollTimeout)
    }

    @Test
    fun `consumer should be instantiated with correct parameters`() {
        assertNotNull(consumer)
    }
}
