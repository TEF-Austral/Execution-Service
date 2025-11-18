package controllers

import dtos.AnalyzerRuleDTO
import dtos.FormatterRuleDTO
import dtos.UpdateAnalyzerConfigRequestDTO
import dtos.UpdateFormatterConfigRequestDTO
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import security.AuthenticatedUserProvider
import services.AnalyzerConfigService
import services.FormatterConfigService

class ConfigurationControllerTest {

    private lateinit var analyzerConfigService: AnalyzerConfigService
    private lateinit var formatterConfigService: FormatterConfigService
    private lateinit var authenticatedUserProvider: AuthenticatedUserProvider
    private lateinit var controller: ConfigurationController

    @BeforeEach
    fun setup() {
        analyzerConfigService = mockk(relaxed = true)
        formatterConfigService = mockk(relaxed = true)
        authenticatedUserProvider = mockk(relaxed = true)
        controller =
            ConfigurationController(
                analyzerConfigService,
                formatterConfigService,
                authenticatedUserProvider,
            )
    }

    @Test
    fun `getAnalyzerConfig should return analyzer rules for current user`() {
        val userId = "user-123"
        val rules =
            listOf(
                AnalyzerRuleDTO("id1", "rule1", true),
                AnalyzerRuleDTO("id2", "rule2", false),
            )

        every { authenticatedUserProvider.getCurrentUserId() } returns userId
        every { analyzerConfigService.getConfig(userId) } returns rules

        val response = controller.getAnalyzerConfig()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body!!.size)
        assertEquals("rule1", response.body!![0].name)

        verify { authenticatedUserProvider.getCurrentUserId() }
        verify { analyzerConfigService.getConfig(userId) }
    }

    @Test
    fun `getAnalyzerConfig should return empty list when no rules configured`() {
        val userId = "user-123"
        val rules = emptyList<AnalyzerRuleDTO>()

        every { authenticatedUserProvider.getCurrentUserId() } returns userId
        every { analyzerConfigService.getConfig(userId) } returns rules

        val response = controller.getAnalyzerConfig()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(0, response.body!!.size)

        verify { authenticatedUserProvider.getCurrentUserId() }
        verify { analyzerConfigService.getConfig(userId) }
    }

    @Test
    fun `updateAnalyzerConfig should update and return analyzer rules for current user`() {
        val userId = "user-123"
        val requestRules =
            listOf(
                AnalyzerRuleDTO("id1", "rule1", true),
                AnalyzerRuleDTO("id2", "rule2", false),
            )
        val request = UpdateAnalyzerConfigRequestDTO(requestRules)
        val updatedRules =
            listOf(
                AnalyzerRuleDTO("id1", "rule1", true),
                AnalyzerRuleDTO("id2", "rule2", false),
            )

        every { authenticatedUserProvider.getCurrentUserId() } returns userId
        every { analyzerConfigService.updateConfig(userId, requestRules) } returns updatedRules

        val response = controller.updateAnalyzerConfig(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body!!.size)

        verify { authenticatedUserProvider.getCurrentUserId() }
        verify { analyzerConfigService.updateConfig(userId, requestRules) }
    }

    @Test
    fun `getFormatterConfig should return formatter rules for current user`() {
        val userId = "user-123"
        val rules =
            listOf(
                FormatterRuleDTO("id1", "spaceAroundOperators", true),
                FormatterRuleDTO("id2", "newLineBeforeBrace", false),
            )

        every { authenticatedUserProvider.getCurrentUserId() } returns userId
        every { formatterConfigService.getConfig(userId) } returns rules

        val response = controller.getFormatterConfig()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body!!.size)
        assertEquals("spaceAroundOperators", response.body!![0].name)

        verify { authenticatedUserProvider.getCurrentUserId() }
        verify { formatterConfigService.getConfig(userId) }
    }

    @Test
    fun `getFormatterConfig should return empty list when no rules configured`() {
        val userId = "user-123"
        val rules = emptyList<FormatterRuleDTO>()

        every { authenticatedUserProvider.getCurrentUserId() } returns userId
        every { formatterConfigService.getConfig(userId) } returns rules

        val response = controller.getFormatterConfig()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(0, response.body!!.size)

        verify { authenticatedUserProvider.getCurrentUserId() }
        verify { formatterConfigService.getConfig(userId) }
    }

    @Test
    fun `updateFormatterConfig should update and return formatter rules for current user`() {
        val userId = "user-123"
        val requestRules =
            listOf(
                FormatterRuleDTO("id1", "spaceAroundOperators", true),
                FormatterRuleDTO("id2", "newLineBeforeBrace", false),
            )
        val request = UpdateFormatterConfigRequestDTO(requestRules)
        val updatedRules =
            listOf(
                FormatterRuleDTO("id1", "spaceAroundOperators", true),
                FormatterRuleDTO("id2", "newLineBeforeBrace", false),
            )

        every { authenticatedUserProvider.getCurrentUserId() } returns userId
        every { formatterConfigService.updateConfig(userId, requestRules) } returns updatedRules

        val response = controller.updateFormatterConfig(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body!!.size)

        verify { authenticatedUserProvider.getCurrentUserId() }
        verify { formatterConfigService.updateConfig(userId, requestRules) }
    }
}
