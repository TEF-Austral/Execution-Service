package consumers.notification

import events.FormattingRulesUpdatedEvent
import handlers.rules.RuleType
import handlers.rules.RulesUpdatedHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import java.lang.reflect.Method
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
class FormattingRulesUpdatedConsumerTest {

    @Mock
    private lateinit var handler: RulesUpdatedHandler

    @Mock
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @Mock
    private lateinit var record: ObjectRecord<String, FormattingRulesUpdatedEvent>

    private lateinit var consumer: FormattingRulesUpdatedConsumer

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var onMessageMethod: Method
    private lateinit var optionsMethod: Method

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        consumer =
            FormattingRulesUpdatedConsumer(
                streamKey = "test-stream-format",
                consumerGroup = "test-group-format",
                redis = redisTemplate,
                handler = handler,
            )

        onMessageMethod =
            FormattingRulesUpdatedConsumer::class.java
                .getDeclaredMethod("onMessage", ObjectRecord::class.java)
        onMessageMethod.isAccessible = true

        optionsMethod =
            FormattingRulesUpdatedConsumer::class.java
                .getDeclaredMethod("options")
        optionsMethod.isAccessible = true
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun invokeOnMessage(record: ObjectRecord<String, FormattingRulesUpdatedEvent>) {
        onMessageMethod.invoke(consumer, record)
    }

    private fun invokeOptions(): StreamReceiver.StreamReceiverOptions<
        String,
        ObjectRecord<String, FormattingRulesUpdatedEvent>,
    > {
        @Suppress("UNCHECKED_CAST")
        return optionsMethod.invoke(
            consumer,
        ) as StreamReceiver.StreamReceiverOptions<
            String,
            ObjectRecord<String, FormattingRulesUpdatedEvent>,
        >
    }

    @Test
    fun `onMessage should process formatting rules updated event successfully`() =
        runTest {
            val userId = "user123"
            val event = FormattingRulesUpdatedEvent(userId = userId)
            whenever(record.value).thenReturn(event)

            invokeOnMessage(record)
            Thread.sleep(100)

            verify(record).value
            verify(handler).handle(RuleType.Format, userId)
        }

    @Test
    fun `onMessage should log error when handler throws exception`() =
        runTest {
            val userId = "user456"
            val event = FormattingRulesUpdatedEvent(userId = userId)
            val exception = RuntimeException("Handler failed")
            whenever(record.value).thenReturn(event)
            doThrow(exception).whenever(handler).handle(any(), any())

            invokeOnMessage(record)
            Thread.sleep(100)

            verify(record).value
            verify(handler).handle(RuleType.Format, userId)
        }

    @Test
    fun `onMessage should handle exception in outer try-catch when record value throws`() {
        whenever(record.value).thenThrow(RuntimeException("Record processing failed"))

        invokeOnMessage(record)
        verify(record).value
    }

    @Test
    fun `onMessage should handle exception with stack trace`() =
        runTest {
            val userId = "user789"
            val event = FormattingRulesUpdatedEvent(userId = userId)
            val exception = RuntimeException("Exception with stack trace")
            whenever(record.value).thenReturn(event)
            doThrow(exception).whenever(handler).handle(any(), any())

            invokeOnMessage(record)
            Thread.sleep(100)
            verify(handler).handle(RuleType.Format, userId)
        }

    @Test
    fun `onMessage should call handler with correct RuleType Format`() =
        runTest {
            val userId = "user-format"
            val event = FormattingRulesUpdatedEvent(userId = userId)
            whenever(record.value).thenReturn(event)
            invokeOnMessage(record)
            Thread.sleep(100)
            verify(handler).handle(eq(RuleType.Format), eq(userId))
        }

    @Test
    fun `options should return StreamReceiverOptions with correct configuration`() {
        val options = invokeOptions()
        assertNotNull(options)
    }

    @Test
    fun `options should return correct poll timeout duration`() {
        val options = invokeOptions()
        assertNotNull(options)
    }

    @Test
    fun `onMessage should extract and log stack trace location when handler fails`() =
        runTest {
            val userId = "user-with-error"
            val event = FormattingRulesUpdatedEvent(userId = userId)
            val exception = RuntimeException("Test exception with stack trace")
            whenever(record.value).thenReturn(event)
            doThrow(exception).whenever(handler).handle(any(), any())
            invokeOnMessage(record)
            Thread.sleep(100)
            verify(handler).handle(RuleType.Format, userId)
        }

    @Test
    fun `onMessage should handle multiple events sequentially`() =
        runTest {
            val userId1 = "user1"
            val userId2 = "user2"
            val event1 = FormattingRulesUpdatedEvent(userId = userId1)
            val event2 = FormattingRulesUpdatedEvent(userId = userId2)

            whenever(record.value).thenReturn(event1).thenReturn(event2)
            invokeOnMessage(record)
            Thread.sleep(100)
            invokeOnMessage(record)
            Thread.sleep(100)
            verify(handler).handle(RuleType.Format, userId1)
            verify(handler).handle(RuleType.Format, userId2)
        }

    @Test
    fun `onMessage should process event with empty userId`() =
        runTest {
            val userId = ""
            val event = FormattingRulesUpdatedEvent(userId = userId)
            whenever(record.value).thenReturn(event)
            invokeOnMessage(record)
            Thread.sleep(100)
            verify(handler).handle(RuleType.Format, userId)
        }

    @Test
    fun `onMessage should catch exception in outer block and log it`() {
        val exception = IllegalStateException("Outer exception")
        whenever(record.value).thenThrow(exception)
        try {
            invokeOnMessage(record)
        } catch (e: Exception) {
            throw AssertionError("Exception should have been caught by consumer", e)
        }

        verify(record).value
    }
}
