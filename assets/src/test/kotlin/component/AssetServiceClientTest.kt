package component

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
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
    fun testGetAssetSuccess() {
        val container = "test-container"
        val key = "test-key"
        val expectedContent = "let x: number = 5;"
        val url = "http://localhost:8080/$container/$key"

        `when`(restTemplate.getForObject(url, String::class.java)).thenReturn(expectedContent)

        val result = client.getAsset(container, key)

        assertEquals(expectedContent, result)
        verify(restTemplate).getForObject(url, String::class.java)
    }

    @Test
    fun testGetAssetNotFound() {
        val container = "test-container"
        val key = "missing-key"
        val url = "http://localhost:8080/$container/$key"

        `when`(restTemplate.getForObject(url, String::class.java)).thenReturn(null)

        val exception =
            assertThrows(NoSuchElementException::class.java) {
                client.getAsset(container, key)
            }

        assertEquals("Asset not found: $container/$key", exception.message)
        verify(restTemplate).getForObject(url, String::class.java)
    }

    @Test
    fun testGetAssetWithDifferentContainerAndKey() {
        val container = "snippets"
        val key = "snippet-123"
        val expectedContent = "println(42);"
        val url = "http://localhost:8080/$container/$key"

        `when`(restTemplate.getForObject(url, String::class.java)).thenReturn(expectedContent)

        val result = client.getAsset(container, key)

        assertEquals(expectedContent, result)
    }

    @Test
    fun testCreateOrUpdateAssetSuccess() {
        val container = "container"
        val key = "key"
        val content = "content"
        val url = "http://localhost:8080/$container/$key"
        val response = ResponseEntity<String>(HttpStatus.OK)

        `when`(
            restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PUT),
                any(HttpEntity::class.java),
                eq(String::class.java),
            ),
        ).thenReturn(response)

        client.createOrUpdateAsset(container, key, content)

        verify(restTemplate).exchange(
            eq(url),
            eq(HttpMethod.PUT),
            any(HttpEntity::class.java),
            eq(String::class.java),
        )
    }

    @Test
    fun testCreateOrUpdateAssetWithLongContent() {
        val container = "test-container"
        val key = "test-key"
        val content = "let x: number = 5;\nlet y: string = 'hello';\nprintln(x);\nprintln(y);"
        val url = "http://localhost:8080/$container/$key"
        val response = ResponseEntity<String>(HttpStatus.OK)

        `when`(
            restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PUT),
                any(HttpEntity::class.java),
                eq(String::class.java),
            ),
        ).thenReturn(response)

        client.createOrUpdateAsset(container, key, content)

        verify(restTemplate).exchange(
            eq(url),
            eq(HttpMethod.PUT),
            any(HttpEntity::class.java),
            eq(String::class.java),
        )
    }

    @Test
    fun testCreateOrUpdateAssetWithEmptyContent() {
        val container = "test-container"
        val key = "test-key"
        val content = ""
        val url = "http://localhost:8080/$container/$key"
        val response = ResponseEntity<String>(HttpStatus.OK)

        `when`(
            restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PUT),
                any(HttpEntity::class.java),
                eq(String::class.java),
            ),
        ).thenReturn(response)

        client.createOrUpdateAsset(container, key, content)

        verify(restTemplate).exchange(
            eq(url),
            eq(HttpMethod.PUT),
            any(HttpEntity::class.java),
            eq(String::class.java),
        )
    }

    @Test
    fun testGetAssetWithSpecialCharacters() {
        val container = "special-container"
        val key = "key-with-dashes"
        val expectedContent = "special content"
        val url = "http://localhost:8080/$container/$key"

        `when`(restTemplate.getForObject(url, String::class.java)).thenReturn(expectedContent)

        val result = client.getAsset(container, key)

        assertEquals(expectedContent, result)
    }

    @Test
    fun testCreateOrUpdateAssetCreated() {
        val container = "new-container"
        val key = "new-key"
        val content = "new content"
        val url = "http://localhost:8080/$container/$key"
        val response = ResponseEntity<String>(HttpStatus.CREATED)

        `when`(
            restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PUT),
                any(HttpEntity::class.java),
                eq(String::class.java),
            ),
        ).thenReturn(response)

        client.createOrUpdateAsset(container, key, content)

        verify(restTemplate).exchange(
            eq(url),
            eq(HttpMethod.PUT),
            any(HttpEntity::class.java),
            eq(String::class.java),
        )
    }
}
