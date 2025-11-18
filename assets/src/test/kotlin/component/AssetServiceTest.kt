package component

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AssetServiceTest {

    private class TestAssetService : AssetService {
        private val storage = mutableMapOf<String, String>()

        override fun getAsset(
            container: String,
            key: String,
        ): String {
            val fullKey = "$container/$key"
            return storage[fullKey] ?: throw NoSuchElementException("Asset not found: $fullKey")
        }

        override fun createOrUpdateAsset(
            container: String,
            key: String,
            content: String,
        ) {
            val fullKey = "$container/$key"
            storage[fullKey] = content
        }
    }

    @Test
    fun testAssetServiceImplementation() {
        val service: AssetService = TestAssetService()
        val container = "test-container"
        val key = "test-key"
        val content = "test content"

        service.createOrUpdateAsset(container, key, content)
        val result = service.getAsset(container, key)

        assertEquals(content, result)
    }

    @Test
    fun testAssetServiceMultipleAssets() {
        val service: AssetService = TestAssetService()

        service.createOrUpdateAsset("container1", "key1", "content1")
        service.createOrUpdateAsset("container2", "key2", "content2")

        assertEquals("content1", service.getAsset("container1", "key1"))
        assertEquals("content2", service.getAsset("container2", "key2"))
    }

    @Test
    fun testAssetServiceUpdate() {
        val service: AssetService = TestAssetService()
        val container = "test-container"
        val key = "test-key"

        service.createOrUpdateAsset(container, key, "original content")
        service.createOrUpdateAsset(container, key, "updated content")

        assertEquals("updated content", service.getAsset(container, key))
    }

    @Test
    fun testAssetServiceGetNonExistent() {
        val service: AssetService = TestAssetService()

        try {
            service.getAsset("nonexistent", "key")
            throw AssertionError("Expected NoSuchElementException")
        } catch (e: NoSuchElementException) {
            assertEquals("Asset not found: nonexistent/key", e.message)
        }
    }
}
