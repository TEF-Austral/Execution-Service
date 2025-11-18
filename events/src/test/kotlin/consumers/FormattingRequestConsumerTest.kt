package consumers

import consumers.handlers.IFormattingRequestHandler
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
import requests.FormattingRequestEvent
import java.lang.reflect.Method
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class FormattingRequestConsumerTest {

    private lateinit var handler: IFormattingRequestHandler
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var consumer: FormattingRequestConsumer
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var onMessageMethod: Method
    private lateinit var optionsMethod: Method

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        handler = mockk(relaxed = true)
        redisTemplate = mockk(relaxed = true)
        consumer =
            FormattingRequestConsumer(
                streamKey = "test-formatting-stream",
                consumerGroup = "test-group",
                redis = redisTemplate,
                handler = handler,
            )

        onMessageMethod =
            FormattingRequestConsumer::class.java
                .getDeclaredMethod("onMessage", ObjectRecord::class.java)
        onMessageMethod.isAccessible = true

        optionsMethod =
            FormattingRequestConsumer::class.java
                .getDeclaredMethod("options")
        optionsMethod.isAccessible = true
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onMessage should call handler with formatting request event`() {
        val event =
            FormattingRequestEvent(
                requestId = "req-123",
                snippetId = 1L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
                userId = "user-123",
                languageId = "PRINTSCRIPT",
            )
        val record: ObjectRecord<String, FormattingRequestEvent> =
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
                ObjectRecord<String, FormattingRequestEvent>,
            >

        assertNotNull(options)
        assertEquals(Duration.ofMillis(10000), options.pollTimeout)
    }

    @Test
    fun `consumer should be instantiated with correct parameters`() {
        assertNotNull(consumer)
    }
}
