package consumers

import consumers.handlers.ILintingRequestHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.austral.ingsis.redis.RedisStreamConsumer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component
import requests.LintingRequestEvent
import java.time.Duration

@Component
class LintingRequestConsumer(
    @Value("\${spring.redis.stream.linting.request.key}") streamKey: String,
    @Value("\${spring.redis.consumer.group}") consumerGroup: String,
    redis: RedisTemplate<String, String>,
    private val handler: ILintingRequestHandler,
) : RedisStreamConsumer<LintingRequestEvent>(streamKey, consumerGroup, redis) {

    private val log = LoggerFactory.getLogger(LintingRequestConsumer::class.java)

    override fun onMessage(record: ObjectRecord<String, LintingRequestEvent>) {
        log.info(
            "Received linting request: snippetId=${record.value.snippetId}, requestId=${record.value.requestId}",
        )
        GlobalScope.launch(Dispatchers.IO) {
            handler.handle(record.value)
        }
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
