package consumers

import TestingRequestEvent
import handlers.TestingRequestHandler
import org.austral.ingsis.redis.RedisStreamConsumer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component
import java.time.Duration

@Component
@Profile("!test")
class TestingRequestConsumer(
    @Value("\${redis.stream.testing.request.key}") streamKey: String,
    @Value("\${redis.consumer.group}") consumerGroup: String,
    redis: RedisTemplate<String, String>,
    private val handler: TestingRequestHandler,
) : RedisStreamConsumer<TestingRequestEvent>(streamKey, consumerGroup, redis) {

    override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, TestingRequestEvent>> {
        return StreamReceiver.StreamReceiverOptions.builder()
            .pollTimeout(Duration.ofMillis(10000))
            .targetType(TestingRequestEvent::class.java)
            .build()
    }

    override fun onMessage(record: ObjectRecord<String, TestingRequestEvent>) {
        val event = record.value
        println("📨 [PrintScript Service] Received testing REQUEST: ${event.requestId}")
        handler.handleTestingRequest(event)
    }
}
