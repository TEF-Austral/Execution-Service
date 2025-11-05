package services

import checkers.IdentifierStyle
import dtos.AnalyzerRuleDTO
import entities.AnalyzerEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import repositories.AnalyzerRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class AnalyzerConfigServiceTest {

    @Mock
    private lateinit var analyzerRepository: AnalyzerRepository

    @InjectMocks
    private lateinit var analyzerConfigService: AnalyzerConfigService

    private val testUserId = "user123"

    @BeforeEach
    fun setUp() {
        reset(analyzerRepository)
    }

    @Test
    fun `getConfig should return default rules when user does not exist`() {
        val defaultEntity = AnalyzerEntity(userId = testUserId)

        `when`(analyzerRepository.findById(testUserId)).thenReturn(Optional.empty())
        `when`(analyzerRepository.save(any())).thenReturn(defaultEntity)

        val result = analyzerConfigService.getConfig(testUserId)

        verify(analyzerRepository, times(1)).save(any())

        assertEquals(4, result.size)

        val identifierStyleRule = result.find { it.id == "identifierStyle" }
        assertNotNull(identifierStyleRule)
        assertEquals("Identifier Style", identifierStyleRule?.name)
        assertEquals(false, identifierStyleRule?.isActive)
        assertEquals("NO_STYLE", identifierStyleRule?.value)

        val restrictPrintlnRule = result.find { it.id == "restrictPrintlnArgs" }
        assertNotNull(restrictPrintlnRule)
        assertEquals(true, restrictPrintlnRule?.isActive)
        assertNull(restrictPrintlnRule?.value)

        val restrictReadInputRule = result.find { it.id == "restrictReadInputArgs" }
        assertNotNull(restrictReadInputRule)
        assertEquals(false, restrictReadInputRule?.isActive)

        val noReadInputRule = result.find { it.id == "noReadInput" }
        assertNotNull(noReadInputRule)
        assertEquals(false, noReadInputRule?.isActive)
    }

    @Test
    fun `getConfig should return user configuration when user exists`() {
        val entity =
            AnalyzerEntity(
                userId = testUserId,
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = true,
                noReadInput = true,
            )
        `when`(analyzerRepository.findById(testUserId)).thenReturn(Optional.of(entity))

        val result = analyzerConfigService.getConfig(testUserId)

        assertEquals(4, result.size)

        val identifierStyleRule = result.find { it.id == "identifierStyle" }
        assertEquals(true, identifierStyleRule?.isActive)
        assertEquals("CAMEL_CASE", identifierStyleRule?.value)

        val restrictPrintlnRule = result.find { it.id == "restrictPrintlnArgs" }
        assertEquals(false, restrictPrintlnRule?.isActive)

        val restrictReadInputRule = result.find { it.id == "restrictReadInputArgs" }
        assertEquals(true, restrictReadInputRule?.isActive)

        val noReadInputRule = result.find { it.id == "noReadInput" }
        assertEquals(true, noReadInputRule?.isActive)
    }

    @Test
    fun `updateConfig should create new entity when user does not exist`() {
        `when`(analyzerRepository.findById(testUserId)).thenReturn(Optional.empty())

        val rules =
            listOf(
                AnalyzerRuleDTO("identifierStyle", "Identifier Style", true, "SNAKE_CASE"),
                AnalyzerRuleDTO("restrictPrintlnArgs", "Restrict Println Arguments", true, null),
                AnalyzerRuleDTO(
                    "restrictReadInputArgs",
                    "Restrict Read Input Arguments",
                    false,
                    null,
                ),
                AnalyzerRuleDTO("noReadInput", "No Read Input", false, null),
            )

        val expectedEntity =
            AnalyzerEntity(
                userId = testUserId,
                identifierStyle = IdentifierStyle.SNAKE_CASE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = false,
                noReadInput = false,
            )

        `when`(analyzerRepository.save(any())).thenReturn(expectedEntity)

        val result = analyzerConfigService.updateConfig(testUserId, rules)

        verify(analyzerRepository, times(1)).save(any())

        val identifierStyleRule = result.find { it.id == "identifierStyle" }
        assertEquals(true, identifierStyleRule?.isActive)
        assertEquals("SNAKE_CASE", identifierStyleRule?.value)
    }

    @Test
    fun `updateConfig should update existing entity`() {
        val existingEntity =
            AnalyzerEntity(
                userId = testUserId,
                identifierStyle = IdentifierStyle.NO_STYLE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = false,
                noReadInput = false,
            )
        `when`(analyzerRepository.findById(testUserId)).thenReturn(Optional.of(existingEntity))

        val rules =
            listOf(
                AnalyzerRuleDTO("identifierStyle", "Identifier Style", true, "CAMEL_CASE"),
                AnalyzerRuleDTO("restrictPrintlnArgs", "Restrict Println Arguments", false, null),
                AnalyzerRuleDTO(
                    "restrictReadInputArgs",
                    "Restrict Read Input Arguments",
                    true,
                    null,
                ),
                AnalyzerRuleDTO("noReadInput", "No Read Input", true, null),
            )

        val updatedEntity =
            AnalyzerEntity(
                userId = testUserId,
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = true,
                noReadInput = true,
            )

        `when`(analyzerRepository.save(any())).thenReturn(updatedEntity)

        val result = analyzerConfigService.updateConfig(testUserId, rules)

        verify(analyzerRepository, times(1)).save(any())

        val identifierStyleRule = result.find { it.id == "identifierStyle" }
        assertEquals(true, identifierStyleRule?.isActive)
        assertEquals("CAMEL_CASE", identifierStyleRule?.value)

        val restrictPrintlnRule = result.find { it.id == "restrictPrintlnArgs" }
        assertEquals(false, restrictPrintlnRule?.isActive)

        val restrictReadInputRule = result.find { it.id == "restrictReadInputArgs" }
        assertEquals(true, restrictReadInputRule?.isActive)

        val noReadInputRule = result.find { it.id == "noReadInput" }
        assertEquals(true, noReadInputRule?.isActive)
    }

    @Test
    fun `updateConfig should handle identifier style deactivation`() {
        val existingEntity =
            AnalyzerEntity(
                userId = testUserId,
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = false,
                noReadInput = false,
            )
        `when`(analyzerRepository.findById(testUserId)).thenReturn(Optional.of(existingEntity))

        val rules =
            listOf(
                AnalyzerRuleDTO("identifierStyle", "Identifier Style", false, "CAMEL_CASE"),
                AnalyzerRuleDTO("restrictPrintlnArgs", "Restrict Println Arguments", true, null),
                AnalyzerRuleDTO(
                    "restrictReadInputArgs",
                    "Restrict Read Input Arguments",
                    false,
                    null,
                ),
                AnalyzerRuleDTO("noReadInput", "No Read Input", false, null),
            )

        val updatedEntity =
            AnalyzerEntity(
                userId = testUserId,
                identifierStyle = IdentifierStyle.NO_STYLE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = false,
                noReadInput = false,
            )

        `when`(analyzerRepository.save(any())).thenReturn(updatedEntity)

        val result = analyzerConfigService.updateConfig(testUserId, rules)

        val identifierStyleRule = result.find { it.id == "identifierStyle" }
        assertEquals(false, identifierStyleRule?.isActive)
        assertEquals("NO_STYLE", identifierStyleRule?.value)
    }

    @Test
    fun `updateConfig should handle invalid identifier style value`() {
        `when`(analyzerRepository.findById(testUserId)).thenReturn(Optional.empty())

        val rules =
            listOf(
                AnalyzerRuleDTO("identifierStyle", "Identifier Style", true, "INVALID_STYLE"),
                AnalyzerRuleDTO("restrictPrintlnArgs", "Restrict Println Arguments", true, null),
                AnalyzerRuleDTO(
                    "restrictReadInputArgs",
                    "Restrict Read Input Arguments",
                    false,
                    null,
                ),
                AnalyzerRuleDTO("noReadInput", "No Read Input", false, null),
            )

        val savedEntity =
            AnalyzerEntity(
                userId = testUserId,
                identifierStyle = IdentifierStyle.NO_STYLE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = false,
                noReadInput = false,
            )

        `when`(analyzerRepository.save(any())).thenReturn(savedEntity)

        val result = analyzerConfigService.updateConfig(testUserId, rules)

        val identifierStyleRule = result.find { it.id == "identifierStyle" }
        assertEquals(false, identifierStyleRule?.isActive)
        assertEquals("NO_STYLE", identifierStyleRule?.value)
    }

    @Test
    fun `updateConfig should handle partial rule updates`() {
        val existingEntity =
            AnalyzerEntity(
                userId = testUserId,
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = false,
                noReadInput = false,
            )
        `when`(analyzerRepository.findById(testUserId)).thenReturn(Optional.of(existingEntity))

        val rules =
            listOf(
                AnalyzerRuleDTO("noReadInput", "No Read Input", true, null),
            )

        val updatedEntity =
            AnalyzerEntity(
                userId = testUserId,
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = false,
                noReadInput = true,
            )

        `when`(analyzerRepository.save(any())).thenReturn(updatedEntity)

        val result = analyzerConfigService.updateConfig(testUserId, rules)

        verify(analyzerRepository, times(1)).save(any())

        val noReadInputRule = result.find { it.id == "noReadInput" }
        assertEquals(true, noReadInputRule?.isActive)
    }

    @Test
    fun `AnalyzerRuleDTO should be created correctly`() {
        val rule =
            AnalyzerRuleDTO(
                id = "testId",
                name = "Test Rule",
                isActive = true,
                value = "testValue",
            )

        assertEquals("testId", rule.id)
        assertEquals("Test Rule", rule.name)
        assertTrue(rule.isActive)
        assertEquals("testValue", rule.value)
    }

    @Test
    fun `AnalyzerRuleDTO should handle null value`() {
        val rule =
            AnalyzerRuleDTO(
                id = "testId",
                name = "Test Rule",
                isActive = false,
                value = null,
            )
        assertNull(rule.value)
        assertFalse(rule.isActive)
    }
}
