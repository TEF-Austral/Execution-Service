package helpers

import checkers.IdentifierStyle
import config.AnalyzerConfig
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import kotlin.test.assertEquals

class UserConfigProviderTest {

    @Test
    fun `getUserConfig returns default config for unknown user`() {
        val provider = UserConfigProvider()

        val config = provider.getUserConfig("unknown")

        assertNotNull(actual = config)
        assertEquals(IdentifierStyle.NO_STYLE, config.identifierStyle)
    }

    @Test
    fun `getUserConfig returns default config for null userId`() {
        val provider = UserConfigProvider()

        val config = provider.getUserConfig(null)

        assertNotNull(config)
    }

    @Test
    fun `setUserConfig and getUserConfig work together`() {
        val provider = UserConfigProvider()
        val customConfig =
            AnalyzerConfig(
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = true,
                noReadInput = true,
            )

        provider.setUserConfig("user123", customConfig)
        val retrievedConfig = provider.getUserConfig("user123")

        assertEquals(IdentifierStyle.CAMEL_CASE, retrievedConfig.identifierStyle)
        assertFalse(retrievedConfig.restrictPrintlnArgs)
        assertTrue(retrievedConfig.restrictReadInputArgs)
        assertTrue(retrievedConfig.noReadInput)
    }

    @Test
    fun `setUserConfig overwrites existing config`() {
        val provider = UserConfigProvider()
        val config1 = AnalyzerConfig(identifierStyle = IdentifierStyle.CAMEL_CASE)
        val config2 = AnalyzerConfig(identifierStyle = IdentifierStyle.SNAKE_CASE)

        provider.setUserConfig("user123", config1)
        provider.setUserConfig("user123", config2)
        val retrievedConfig = provider.getUserConfig("user123")

        assertEquals(IdentifierStyle.SNAKE_CASE, retrievedConfig.identifierStyle)
    }

    @Test
    fun `multiple users have independent configs`() {
        val provider = UserConfigProvider()
        val config1 = AnalyzerConfig(identifierStyle = IdentifierStyle.CAMEL_CASE)
        val config2 = AnalyzerConfig(identifierStyle = IdentifierStyle.SNAKE_CASE)

        provider.setUserConfig("user1", config1)
        provider.setUserConfig("user2", config2)

        assertEquals(IdentifierStyle.CAMEL_CASE, provider.getUserConfig("user1").identifierStyle)
        assertEquals(IdentifierStyle.SNAKE_CASE, provider.getUserConfig("user2").identifierStyle)
    }
}
