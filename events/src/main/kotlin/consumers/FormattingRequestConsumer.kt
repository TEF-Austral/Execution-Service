package consumers

import consumers.handlers.IFormattingRequestHandler
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
import requests.FormattingRequestEvent
import java.time.Duration

@Component
class FormattingRequestConsumer(
    @Value("\${spring.redis.stream.formatting.request.key}") streamKey: String,
    @Value("\${spring.redis.consumer.group}") consumerGroup: String,
    redis: RedisTemplate<String, String>,
    private val handler: IFormattingRequestHandler,
) : RedisStreamConsumer<FormattingRequestEvent>(streamKey, consumerGroup, redis) {

    private val log = LoggerFactory.getLogger(FormattingRequestConsumer::class.java)

    override fun onMessage(record: ObjectRecord<String, FormattingRequestEvent>) {
        log.info(
            "Received formatting request: snippetId=${record.value.snippetId}, requestId=${record.value.requestId}",
        )
        GlobalScope.launch(Dispatchers.IO) {
            handler.handle(record.value)
        }
    }

    override fun options(): StreamReceiver.StreamReceiverOptions<
        String,
        ObjectRecord<
            String,
            FormattingRequestEvent,
        >,
    > =
        StreamReceiver.StreamReceiverOptions
            .builder()
            .pollTimeout(Duration.ofMillis(10000))
            .targetType(FormattingRequestEvent::class.java)
            .build()
}
