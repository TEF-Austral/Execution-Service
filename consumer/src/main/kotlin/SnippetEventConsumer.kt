import com.github.tef.SnippetEventHandler
import jakarta.annotation.PostConstruct
import java.net.InetAddress
import java.time.Duration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Value
import reactor.core.publisher.Flux
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.ObjectRecord
import com.github.tef.events.SnippetEvent

@Component
@Profile("!test")
class SnippetEventConsumer(
    @param:Value($$"${redis.stream.snippet.key}") private val streamKey: String,
    @param:Value($$"${redis.consumer.group}") private val groupId: String,
    private val redis: ReactiveRedisTemplate<String, String>,
    private val handler: SnippetEventHandler,
) {

    private lateinit var flow: Flux<ObjectRecord<String, SnippetEvent>>

    @PostConstruct
    fun subscription() {
        println(
            "🔌 Initializing com.github.tef.SnippetEventConsumer for stream: $streamKey, group: $groupId",
        )

        try {
            val consumerGroupExists = consumerGroupExists(streamKey, groupId)
            if (!consumerGroupExists) {
                println("📝 Creating consumer group $groupId for stream $streamKey")
                createConsumerGroup(streamKey, groupId)
            } else {
                println("✅ Consumer group $groupId already exists for stream $streamKey")
            }
        } catch (e: Exception) {
            println("⚠️ Exception checking consumer group: ${e.message}")
            println("📝 Creating stream $streamKey and group $groupId")
            redis.opsForStream<Any, Any>().createGroup(streamKey, groupId).block()
        }

        val options =
            StreamReceiver.StreamReceiverOptions
                .builder()
                .pollTimeout(Duration.ofMillis(10000))
                .targetType(SnippetEvent::class.java)
                .build()

        val factory = redis.connectionFactory
        val container = StreamReceiver.create(factory, options)

        flow =
            container.receiveAutoAck(
                Consumer.from(groupId, InetAddress.getLocalHost().hostName),
                StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
            )

        flow.subscribe { record: ObjectRecord<String, SnippetEvent> ->
            println("📨 Received event: ${record.id}")
            handler.handleSnippetEvent(record.value)
        }

        println("✅ com.github.tef.SnippetEventConsumer initialized successfully")
    }

    private fun createConsumerGroup(
        streamKey: String,
        groupId: String,
    ): String? = redis.opsForStream<Any, Any>().createGroup(streamKey, groupId).block()

    private fun consumerGroupExists(
        stream: String,
        group: String,
    ): Boolean {
        val groups =
            redis
                .opsForStream<Any, Any>()
                .groups(stream)
                .collectList()
                .block() ?: emptyList()
        return groups.any { it.groupName() == group }
    }
}
