package config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class RedisStreamConfig(
    private val redisTemplate: RedisTemplate<String, String>,
    @Value("\${spring.redis.stream.formatting.request.key}")
    private val formattingRequestKey: String,
    @Value("\${spring.redis.stream.linting.request.key}")
    private val lintingRequestKey: String,
    @Value("\${spring.redis.stream.testing.request.key}")
    private val testingRequestKey: String,
    @Value("\${spring.redis.consumer.group}")
    private val consumerGroup: String,
) {

    @PostConstruct
    fun createConsumerGroups() {
        val streams =
            listOf(
                formattingRequestKey,
                lintingRequestKey,
                testingRequestKey,
            )

        streams.forEach { streamKey ->
            try {
                // Intenta crear el consumer group
                redisTemplate
                    .opsForStream<String, Any>()
                    .createGroup(streamKey, consumerGroup)

                println("✅ Consumer group '$consumerGroup' creado para stream '$streamKey'")
            } catch (e: Exception) {
                // Si falla, probablemente ya existe o el stream no existe
                if (e.message?.contains("BUSYGROUP") == true) {
                    println("ℹ️  Consumer group '$consumerGroup' ya existe para '$streamKey'")
                } else {
                    // Si el stream no existe, lo creamos enviando un mensaje dummy
                    try {
                        redisTemplate
                            .opsForStream<String, Any>()
                            .add(streamKey, mapOf("init" to "true"))

                        // Ahora creamos el grupo
                        redisTemplate
                            .opsForStream<String, Any>()
                            .createGroup(streamKey, ReadOffset.from("0"), consumerGroup)

                        println("✅ Stream '$streamKey' y consumer group '$consumerGroup' creados")
                    } catch (e2: Exception) {
                        println("⚠️  Error creando consumer group para '$streamKey': ${e2.message}")
                    }
                }
            }
        }
    }
}
