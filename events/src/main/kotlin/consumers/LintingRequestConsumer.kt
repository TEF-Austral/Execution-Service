package consumers

import consumers.handlers.ILintingRequestHandler
import kotlinx.coroutines.Dispatchers // <-- 1. IMPORTAR
import kotlinx.coroutines.GlobalScope // <-- 2. IMPORTAR
import kotlinx.coroutines.launch // <-- 3. IMPORTAR
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

    // ESTE ES EL MÉTODO QUE CAMBIA
    override fun onMessage(record: ObjectRecord<String, LintingRequestEvent>) {
        GlobalScope.launch(Dispatchers.IO) {
            handler.handle(record.value) // <-- El handler (bloqueante) se ejecuta en OTRO hilo
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
