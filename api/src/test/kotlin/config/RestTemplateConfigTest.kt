package config

import api.config.RestTemplateConfig
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class RestTemplateConfigTest {

    @Test
    fun `restTemplate should create RestTemplate instance`() {
        val config = RestTemplateConfig()

        val restTemplate = config.restTemplate()

        assertNotNull(restTemplate)
    }
}
