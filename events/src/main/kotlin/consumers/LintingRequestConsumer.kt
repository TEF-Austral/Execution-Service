package consumers

import consumers.handlers.ILintingRequestHandler
import org.austral.ingsis.redis.RedisStreamConsumer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component
import requests.LintingRequestEvent
import java.time.Duration

@Component
@Profile("!test")
class LintingRequestConsumer(
    @Value("\${spring.redis.stream.linting.request.key}") streamKey: String,
    @Value("\${spring.redis.consumer.group}") consumerGroup: String,
    redis: RedisTemplate<String, String>,
    private val handler: ILintingRequestHandler,
) : RedisStreamConsumer<LintingRequestEvent>(streamKey, consumerGroup, redis) {

    override fun onMessage(record: ObjectRecord<String, LintingRequestEvent>) {
        handler.handle(record.value)
    }

    override fun options(): StreamReceiver.StreamReceiverOptions<
        String,
        ObjectRecord<
            String,
            LintingRequestEvent,
        >,
    > =
        StreamReceiver.StreamReceiverOptions
            .builder()
            .pollTimeout(Duration.ofMillis(10000))
            .targetType(LintingRequestEvent::class.java)
            .build()
}
