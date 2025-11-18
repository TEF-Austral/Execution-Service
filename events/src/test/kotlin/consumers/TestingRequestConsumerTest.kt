package consumers

import consumers.handlers.ITestingRequestHandler
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
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import requests.TestingRequestEvent
import java.lang.reflect.Method
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class TestingRequestConsumerTest {

    private lateinit var handler: ITestingRequestHandler
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var consumer: TestingRequestConsumer
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var onMessageMethod: Method
    private lateinit var optionsMethod: Method

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        handler = mockk(relaxed = true)
        redisTemplate = mockk(relaxed = true)
        consumer =
            TestingRequestConsumer(
                streamKey = "test-testing-stream",
                consumerGroup = "test-group",
                redis = redisTemplate,
                handler = handler,
            )

        onMessageMethod =
            TestingRequestConsumer::class.java
                .getDeclaredMethod("onMessage", ObjectRecord::class.java)
        onMessageMethod.isAccessible = true

        optionsMethod =
            TestingRequestConsumer::class.java
                .getDeclaredMethod("options")
        optionsMethod.isAccessible = true
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onMessage should call handler with testing request event`() {
        val event =
            TestingRequestEvent(
                requestId = "req-789",
                snippetId = 3L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
            )
        val record: ObjectRecord<String, TestingRequestEvent> =
            mockk {
                every { value } returns event
            }

        onMessageMethod.invoke(consumer, record)
        Thread.sleep(100)

        verify(exactly = 1) { handler.handle(event) }
    }

    @Test
    fun `onMessage should handle exception from handler gracefully`() {
        val event =
            TestingRequestEvent(
                requestId = "req-error",
                snippetId = 4L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
            )
        val record: ObjectRecord<String, TestingRequestEvent> =
            mockk {
                every { value } returns event
            }
        every { handler.handle(any()) } throws RuntimeException("Handler error")

        assertDoesNotThrow {
            onMessageMethod.invoke(consumer, record)
            Thread.sleep(100)
        }
    }

    @Test
    fun `options should return correct configuration`() {
        @Suppress("UNCHECKED_CAST")
        val options =
            optionsMethod.invoke(consumer) as StreamReceiver.StreamReceiverOptions<
                String,
                ObjectRecord<String, TestingRequestEvent>,
            >

        assertNotNull(options)
        assertEquals(Duration.ofMillis(10000), options.pollTimeout)
    }

    @Test
    fun `consumer should be instantiated with correct parameters`() {
        assertNotNull(consumer)
    }
}
