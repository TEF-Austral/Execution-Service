package services

import dtos.FormatterRuleDTO
import entities.FormatterConfigEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.doNothing
import producers.FormattingRulesUpdatedProducer
import repositories.FormatterConfigRepository
import java.util.Optional

class FormatterConfigServiceTest {

    private val repository = mock(FormatterConfigRepository::class.java)
    private val rulesUpdatedProducer = mock(FormattingRulesUpdatedProducer::class.java)
    private val service = FormatterConfigService(repository, rulesUpdatedProducer)

    @Test
    fun `getConfig should return existing config`() {
        val userId = "user123"
        val entity =
            FormatterConfigEntity(
                id = 1,
                userId = userId,
                spaceBeforeColon = true,
                indentSize = 2,
            )

        `when`(repository.findByUserId(userId)).thenReturn(Optional.of(entity))

        val result = service.getConfig(userId)

        assertNotNull(result)
        val spaceBeforeColonRule = result.find { it.id == "spaceBeforeColon" }
        val indentSizeRule = result.find { it.id == "indentSize" }

        assertEquals(true, spaceBeforeColonRule?.value)
        assertEquals(2, indentSizeRule?.value)
        verify(repository).findByUserId(userId)
    }

    @Test
    fun `getConfig should create default config when not exists`() {
        val userId = "newuser"
        val defaultEntity = FormatterConfigEntity(userId = userId)

        `when`(repository.findByUserId(userId)).thenReturn(Optional.empty())
        `when`(repository.save(any())).thenReturn(defaultEntity)

        val result = service.getConfig(userId)

        assertNotNull(result)
        verify(repository).save(any())
    }

    @Test
    fun `updateConfig should update existing config`() {
        val userId = "user123"
        val existingEntity = FormatterConfigEntity(id = 1, userId = userId)
        val newConfig =
            listOf(
                FormatterRuleDTO(
                    id = "indentSize",
                    name = "Indent Size",
                    isActive = true,
                    value = 2,
                ),
                FormatterRuleDTO(
                    id = "spaceBeforeColon",
                    name = "Space Before Colon",
                    isActive = true,
                    value = true,
                ),
            )

        `when`(repository.findByUserId(userId)).thenReturn(Optional.of(existingEntity))
        `when`(repository.save(any())).thenAnswer { it.arguments[0] }
        doNothing().`when`(rulesUpdatedProducer).emit(any())

        val result = service.updateConfig(userId, newConfig)

        val indentSizeRule = result.find { it.id == "indentSize" }
        val spaceBeforeColonRule = result.find { it.id == "spaceBeforeColon" }

        assertEquals(2, indentSizeRule?.value)
        assertEquals(true, spaceBeforeColonRule?.value)
        verify(repository)
            .save(any())
    }

    @Test
    fun `updateConfig should create new config when not exists`() {
        val userId = "newuser"
        val newConfig =
            listOf(
                FormatterRuleDTO(
                    id = "indentSize",
                    name = "Indent Size",
                    isActive = true,
                    value = 8,
                ),
            )

        `when`(repository.findByUserId(userId)).thenReturn(Optional.empty())
        `when`(repository.save(any())).thenAnswer { it.arguments[0] }
        doNothing().`when`(rulesUpdatedProducer).emit(any())

        val result = service.updateConfig(userId, newConfig)

        val indentSizeRule = result.find { it.id == "indentSize" }
        assertEquals(8, indentSizeRule?.value)
        verify(repository).save(any())
    }
}
