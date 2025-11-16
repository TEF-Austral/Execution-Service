package consumers

import consumers.handlers.TestingRequestHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.austral.ingsis.redis.RedisStreamConsumer
import org.slf4j.LoggerFactory
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
    private val handler: TestingRequestHandler,
) : RedisStreamConsumer<TestingRequestEvent>(streamKey, consumerGroup, redis) {

    private val log = LoggerFactory.getLogger(TestingRequestConsumer::class.java)

    override fun onMessage(record: ObjectRecord<String, TestingRequestEvent>) {
        log.info(
            "Received testing request: snippetId=${record.value.snippetId}, requestId=${record.value.requestId}",
        )

        GlobalScope.launch(Dispatchers.IO) {
            try {
                handler.handle(record.value)
            } catch (e: Exception) {
                log.error("Error handling testing request: ${e.message}", e)
            }
        }
    }

    override fun options(): StreamReceiver.StreamReceiverOptions<
        String,
        ObjectRecord<String, TestingRequestEvent>,
    > =
        StreamReceiver.StreamReceiverOptions
            .builder()
            .pollTimeout(Duration.ofMillis(10000))
            .targetType(TestingRequestEvent::class.java)
            .build()
}
