package helpers

import checkers.IdentifierStyle
import config.AnalyzerConfig
import entities.AnalyzerEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import repositories.AnalyzerRepository
import java.util.Optional

class GetAnalyzerConfigTest {

    private lateinit var analyzerRepository: AnalyzerRepository
    private lateinit var getAnalyzerConfig: GetAnalyzerConfig

    @BeforeEach
    fun setup() {
        analyzerRepository = mock()
        getAnalyzerConfig = GetAnalyzerConfig(analyzerRepository)
    }

    @Test
    fun `getUserConfig should return default config when user not found`() {
        val userId = "unknownUser"

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.empty())

        val config = getAnalyzerConfig.getUserConfig(userId)

        assertNotNull(config)
        assertEquals(IdentifierStyle.NO_STYLE, config.identifierStyle)
        assertTrue(config.restrictPrintlnArgs)
        assertFalse(config.restrictReadInputArgs)
        assertFalse(config.noReadInput)
    }

    @Test
    fun `getUserConfig should convert entity to config correctly with NO_STYLE`() {
        val userId = "user1"
        val entity =
            AnalyzerEntity(
                userId = userId,
                identifierStyle = IdentifierStyle.NO_STYLE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = false,
                noReadInput = false,
            )

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(entity))

        val config = getAnalyzerConfig.getUserConfig(userId)

        assertNotNull(config)
        assertEquals(IdentifierStyle.NO_STYLE, config.identifierStyle)
        assertTrue(config.restrictPrintlnArgs)
        assertFalse(config.restrictReadInputArgs)
        assertFalse(config.noReadInput)
    }

    @Test
    fun `getUserConfig should convert entity to config correctly with CAMEL_CASE`() {
        val userId = "user2"
        val entity =
            AnalyzerEntity(
                userId = userId,
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = true,
                noReadInput = false,
            )

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(entity))

        val config = getAnalyzerConfig.getUserConfig(userId)

        assertNotNull(config)
        assertEquals(IdentifierStyle.CAMEL_CASE, config.identifierStyle)
        assertFalse(config.restrictPrintlnArgs)
        assertTrue(config.restrictReadInputArgs)
        assertFalse(config.noReadInput)
    }

    @Test
    fun `getUserConfig should convert entity to config correctly with SNAKE_CASE`() {
        val userId = "user3"
        val entity =
            AnalyzerEntity(
                userId = userId,
                identifierStyle = IdentifierStyle.SNAKE_CASE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = true,
                noReadInput = true,
            )

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(entity))

        val config = getAnalyzerConfig.getUserConfig(userId)

        assertNotNull(config)
        assertEquals(IdentifierStyle.SNAKE_CASE, config.identifierStyle)
        assertTrue(config.restrictPrintlnArgs)
        assertTrue(config.restrictReadInputArgs)
        assertTrue(config.noReadInput)
    }

    @Test
    fun `getUserConfig should handle all boolean combinations`() {
        val testCases =
            listOf(
                Triple(false, false, false),
                Triple(false, false, true),
                Triple(false, true, false),
                Triple(false, true, true),
                Triple(true, false, false),
                Triple(true, false, true),
                Triple(true, true, false),
                Triple(true, true, true),
            )

        testCases.forEachIndexed { index, (restrictPrintln, restrictReadInput, noReadInput) ->
            val userId = "boolUser$index"
            val entity =
                AnalyzerEntity(
                    userId = userId,
                    identifierStyle = IdentifierStyle.NO_STYLE,
                    restrictPrintlnArgs = restrictPrintln,
                    restrictReadInputArgs = restrictReadInput,
                    noReadInput = noReadInput,
                )

            whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(entity))

            val config = getAnalyzerConfig.getUserConfig(userId)

            assertEquals(restrictPrintln, config.restrictPrintlnArgs)
            assertEquals(restrictReadInput, config.restrictReadInputArgs)
            assertEquals(noReadInput, config.noReadInput)
        }
    }

    @Test
    fun `getUserConfig should handle all identifier styles`() {
        val styles =
            listOf(
                IdentifierStyle.NO_STYLE,
                IdentifierStyle.CAMEL_CASE,
                IdentifierStyle.SNAKE_CASE,
            )

        styles.forEachIndexed { index, style ->
            val userId = "styleUser$index"
            val entity =
                AnalyzerEntity(
                    userId = userId,
                    identifierStyle = style,
                    restrictPrintlnArgs = true,
                    restrictReadInputArgs = false,
                    noReadInput = false,
                )

            whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(entity))

            val config = getAnalyzerConfig.getUserConfig(userId)

            assertEquals(style, config.identifierStyle)
        }
    }

    @Test
    fun `getUserConfig should preserve all entity properties`() {
        val userId = "preserveUser"
        val entity =
            AnalyzerEntity(
                userId = userId,
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = true,
                noReadInput = true,
            )

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(entity))

        val config = getAnalyzerConfig.getUserConfig(userId)

        assertEquals(entity.identifierStyle, config.identifierStyle)
        assertEquals(entity.restrictPrintlnArgs, config.restrictPrintlnArgs)
        assertEquals(entity.restrictReadInputArgs, config.restrictReadInputArgs)
        assertEquals(entity.noReadInput, config.noReadInput)
    }

    @Test
    fun `getUserConfig should return independent configs for different users`() {
        val user1 = "user1"
        val entity1 =
            AnalyzerEntity(
                userId = user1,
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = false,
                noReadInput = false,
            )

        val user2 = "user2"
        val entity2 =
            AnalyzerEntity(
                userId = user2,
                identifierStyle = IdentifierStyle.SNAKE_CASE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = true,
                noReadInput = true,
            )

        whenever(analyzerRepository.findById(user1)).thenReturn(Optional.of(entity1))
        whenever(analyzerRepository.findById(user2)).thenReturn(Optional.of(entity2))

        val config1 = getAnalyzerConfig.getUserConfig(user1)
        val config2 = getAnalyzerConfig.getUserConfig(user2)

        assertEquals(IdentifierStyle.CAMEL_CASE, config1.identifierStyle)
        assertEquals(IdentifierStyle.SNAKE_CASE, config2.identifierStyle)

        assertTrue(config1.restrictPrintlnArgs)
        assertFalse(config2.restrictPrintlnArgs)

        assertFalse(config1.restrictReadInputArgs)
        assertTrue(config2.restrictReadInputArgs)

        assertFalse(config1.noReadInput)
        assertTrue(config2.noReadInput)
    }

    @Test
    fun `getUserConfig default should match AnalyzerConfig defaults`() {
        val userId = "defaultUser"

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.empty())

        val config = getAnalyzerConfig.getUserConfig(userId)
        val defaultConfig = AnalyzerConfig()

        assertEquals(defaultConfig.identifierStyle, config.identifierStyle)
        assertEquals(defaultConfig.restrictPrintlnArgs, config.restrictPrintlnArgs)
        assertEquals(defaultConfig.restrictReadInputArgs, config.restrictReadInputArgs)
        assertEquals(defaultConfig.noReadInput, config.noReadInput)
    }

    @Test
    fun `getUserConfig should work correctly multiple times for same user`() {
        val userId = "sameUser"
        val entity =
            AnalyzerEntity(
                userId = userId,
                identifierStyle = IdentifierStyle.SNAKE_CASE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = true,
                noReadInput = true,
            )

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(entity))

        val config1 = getAnalyzerConfig.getUserConfig(userId)
        val config2 = getAnalyzerConfig.getUserConfig(userId)

        assertEquals(config1.identifierStyle, config2.identifierStyle)
        assertEquals(config1.restrictPrintlnArgs, config2.restrictPrintlnArgs)
        assertEquals(config1.restrictReadInputArgs, config2.restrictReadInputArgs)
        assertEquals(config1.noReadInput, config2.noReadInput)
    }
}
