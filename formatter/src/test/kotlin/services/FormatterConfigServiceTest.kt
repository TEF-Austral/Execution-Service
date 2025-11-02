package services

import dtos.FormatConfigDTO
import entities.FormatterConfigEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import repositories.FormatterConfigRepository
import java.util.Optional

class FormatterConfigServiceTest {

    private val repository = mock(FormatterConfigRepository::class.java)
    private val service = FormatterConfigService(repository)

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

        assertEquals(true, result.spaceBeforeColon)
        assertEquals(2, result.indentSize)
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
        val newConfig = FormatConfigDTO(indentSize = 2, spaceBeforeColon = true)

        `when`(repository.findByUserId(userId)).thenReturn(Optional.of(existingEntity))
        `when`(repository.save(any())).thenAnswer { it.arguments[0] }

        val result = service.updateConfig(userId, newConfig)

        assertEquals(2, result.indentSize)
        assertEquals(true, result.spaceBeforeColon)
        verify(repository).save(any())
    }

    @Test
    fun `updateConfig should create new config when not exists`() {
        val userId = "newuser"
        val newConfig = FormatConfigDTO(indentSize = 8)

        `when`(repository.findByUserId(userId)).thenReturn(Optional.empty())
        `when`(repository.save(any())).thenAnswer { it.arguments[0] }

        val result = service.updateConfig(userId, newConfig)

        assertEquals(8, result.indentSize)
        verify(repository).save(any())
    }
}
