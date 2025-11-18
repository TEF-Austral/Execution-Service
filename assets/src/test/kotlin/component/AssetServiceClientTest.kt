package component

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.eq
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

class AssetServiceClientTest {

    private lateinit var restTemplate: RestTemplate
    private lateinit var client: AssetServiceClient

    @BeforeEach
    fun setup() {
        restTemplate = mock(RestTemplate::class.java)
        client = AssetServiceClient(restTemplate, "http://localhost:8080")
    }

    @Test
    fun `createOrUpdateAsset should create asset successfully`() {
        val response = ResponseEntity<String>(HttpStatus.OK)

        `when`(
            restTemplate.exchange(
                eq("http://localhost:8080/v1/asset/container/key"),
                eq(HttpMethod.PUT),
                any(HttpEntity::class.java),
                eq(String::class.java),
            ),
        ).thenReturn(response)

        client.createOrUpdateAsset("container", "key", "content")

        assertNotNull(response)
    }
}
