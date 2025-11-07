package producers

import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class FormattingResultProducer(
    @Value("\${spring.redis.stream.formatting.result.key}") streamKey: String,
    redis: RedisTemplate<String, String>,
) : RedisStreamProducer(streamKey, redis)

@Component
class LintingResultProducer(
    @Value("\${spring.redis.stream.linting.result.key}") streamKey: String,
    redis: RedisTemplate<String, String>,
) : RedisStreamProducer(streamKey, redis)

@Component
class TestingResultProducer(
    @Value("\${spring.redis.stream.testing.result.key}") streamKey: String,
    redis: RedisTemplate<String, String>,
) : RedisStreamProducer(streamKey, redis)

@Component
class AnalyzerRulesUpdatedProducer(
    @Value("\${spring.redis.stream.rules.analyzer.updated}") streamKey: String,
    redis: RedisTemplate<String, String>,
) : RedisStreamProducer(streamKey, redis)

@Component
class FormattingRulesUpdatedProducer(
    @Value("\${spring.redis.stream.rules.formatter.updated}") streamKey: String,
    redis: RedisTemplate<String, String>,
) : RedisStreamProducer(streamKey, redis)
