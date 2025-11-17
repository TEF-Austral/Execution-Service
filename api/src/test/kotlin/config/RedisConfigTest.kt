package config

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class RedisConfigTest {

    @Test
    fun `lettuceConnectionFactory should create factory with valid configuration`() {
        val config = RedisConfig()

        val factory = config.lettuceConnectionFactory("localhost", 6379)

        assertNotNull(factory)
    }

    @Test
    fun `lettuceConnectionFactory should handle different ports`() {
        val config = RedisConfig()

        val factory = config.lettuceConnectionFactory("localhost", 6380)

        assertNotNull(factory)
    }

    @Test
    fun `lettuceConnectionFactory should handle different hosts`() {
        val config = RedisConfig()

        val factory = config.lettuceConnectionFactory("redis-server", 6379)

        assertNotNull(factory)
    }
}
