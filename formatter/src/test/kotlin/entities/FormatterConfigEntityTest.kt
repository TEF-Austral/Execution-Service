package entities

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FormatterConfigEntityTest {

    @Test
    fun `should create entity with default values`() {
        val entity = FormatterConfigEntity(userId = "test")

        Assertions.assertEquals("test", entity.userId)
        Assertions.assertEquals(false, entity.spaceBeforeColon)
        Assertions.assertEquals(true, entity.spaceAfterColon)
        Assertions.assertEquals(4, entity.indentSize)
    }

    @Test
    fun `should create entity with custom values`() {
        val entity =
            FormatterConfigEntity(
                userId = "test",
                indentSize = 2,
                spaceBeforeColon = true,
            )

        Assertions.assertEquals(2, entity.indentSize)
        Assertions.assertEquals(true, entity.spaceBeforeColon)
    }

    @Test
    fun `copy should work correctly`() {
        val original = FormatterConfigEntity(userId = "test", indentSize = 4)
        val copy = original.copy(indentSize = 8)

        Assertions.assertEquals(4, original.indentSize)
        Assertions.assertEquals(8, copy.indentSize)
    }
}
