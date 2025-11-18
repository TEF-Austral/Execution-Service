package services

import dtos.FormatConfigDTO
import dtos.FormatterRuleDTO
import entities.FormatterConfigEntity
import mappers.FormatterConfigMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import producers.FormattingRulesUpdatedProducer
import repositories.FormatterConfigRepository
import java.util.Optional

class FormatterConfigServiceExtendedTest {

    @Mock
    private lateinit var formatterConfigRepository: FormatterConfigRepository

    @Mock
    private lateinit var rulesUpdatedProducer: FormattingRulesUpdatedProducer

    @Mock
    private lateinit var formatterConfigMapper: FormatterConfigMapper

    private lateinit var formatterConfigService: FormatterConfigService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        formatterConfigService =
            FormatterConfigService(
                formatterConfigRepository,
                rulesUpdatedProducer,
                formatterConfigMapper,
            )
    }

    @Test
    fun `getConfig should return existing config for user`() {
        val userId = "user123"
        val entity = FormatterConfigEntity(id = 1L, userId = userId)
        val rules =
            listOf(
                FormatterRuleDTO("rule1", "Rule 1", true, true),
            )

        `when`(formatterConfigRepository.findByUserId(userId)).thenReturn(Optional.of(entity))
        `when`(formatterConfigMapper.entityToRules(entity)).thenReturn(rules)

        val result = formatterConfigService.getConfig(userId)

        assertEquals(1, result.size)
        assertEquals("rule1", result[0].id)
        verify(formatterConfigRepository).findByUserId(userId)
        verify(formatterConfigMapper).entityToRules(entity)
    }

    @Test
    fun `getConfig should create default config when not found`() {
        val userId = "user456"
        val savedEntity = FormatterConfigEntity(id = 2L, userId = userId)
        val rules =
            listOf(
                FormatterRuleDTO("rule1", "Rule 1", true, false),
            )

        `when`(formatterConfigRepository.findByUserId(userId)).thenReturn(Optional.empty())
        `when`(
            formatterConfigRepository.save(org.mockito.ArgumentMatchers.any()),
        ).thenReturn(savedEntity)
        `when`(formatterConfigMapper.entityToRules(savedEntity)).thenReturn(rules)

        val result = formatterConfigService.getConfig(userId)

        assertEquals(1, result.size)
        verify(formatterConfigRepository).save(org.mockito.ArgumentMatchers.any())
    }

    @Test
    fun `rulesToConfigDTO should delegate to mapper`() {
        val rules =
            listOf(
                FormatterRuleDTO("rule1", "Rule 1", true, true),
            )
        val configDTO = FormatConfigDTO()

        `when`(formatterConfigMapper.rulesToConfigDTO(rules)).thenReturn(configDTO)

        val result = formatterConfigService.rulesToConfigDTO(rules)

        assertEquals(configDTO, result)
        verify(formatterConfigMapper).rulesToConfigDTO(rules)
    }
}
