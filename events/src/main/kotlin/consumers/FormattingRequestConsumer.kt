package consumers

import handlers.FormattingRequestHandler
import org.austral.ingsis.redis.RedisStreamConsumer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component
import requests.FormattingRequestEvent
import java.time.Duration

@Component
@Profile("!test")
class FormattingRequestConsumer(
    @Value("\${redis.stream.formatting.request.key}") streamKey: String,
    @Value("\${redis.consumer.group}") consumerGroup: String,
    redis: RedisTemplate<String, String>,
    private val handler: FormattingRequestHandler,
) : RedisStreamConsumer<FormattingRequestEvent>(streamKey, consumerGroup, redis) {

    override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, FormattingRequestEvent>> {
        return StreamReceiver.StreamReceiverOptions.builder()
            .pollTimeout(Duration.ofMillis(10000))
            .targetType(FormattingRequestEvent::class.java)
            .build()
    }

    override fun onMessage(record: ObjectRecord<String, FormattingRequestEvent>) {
        val event = record.value
        println("📨 [PrintScript Service] Received formatting REQUEST: ${event.requestId}")
        handler.handleFormattingRequest(event)
    }
}
