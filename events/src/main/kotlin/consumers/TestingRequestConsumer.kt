package consumers

import consumers.handlers.ITestingRequestHandler
import kotlinx.coroutines.Dispatchers // added to run handler in IO dispatcher
import kotlinx.coroutines.GlobalScope // added to launch a coroutine
import kotlinx.coroutines.launch // added to launch a coroutine
import org.austral.ingsis.redis.RedisStreamConsumer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component
import requests.TestingRequestEvent
import java.time.Duration

@Component
@Profile("!test")
class TestingRequestConsumer(
    @Value("\${spring.redis.stream.testing.request.key}") streamKey: String,
    @Value("\${spring.redis.consumer.group}") consumerGroup: String,
    redis: RedisTemplate<String, String>,
    private val handler: ITestingRequestHandler,
) : RedisStreamConsumer<TestingRequestEvent>(streamKey, consumerGroup, redis) {

    override fun onMessage(record: ObjectRecord<String, TestingRequestEvent>) {
        GlobalScope.launch(Dispatchers.IO) {
            handler.handle(record.value)
        }
    }

    override fun options(): StreamReceiver.StreamReceiverOptions<
        String,
        ObjectRecord<
            String,
            TestingRequestEvent,
        >,
    > =
        StreamReceiver.StreamReceiverOptions
            .builder()
            .pollTimeout(Duration.ofMillis(10000))
            .targetType(TestingRequestEvent::class.java)
            .build()
}
