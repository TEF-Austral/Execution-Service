package events

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FormattingRulesUpdatedEventTest {

    @Test
    fun `should create FormattingRulesUpdatedEvent`() {
        val event = FormattingRulesUpdatedEvent(userId = "user123")

        assertEquals("user123", event.userId)
    }

    @Test
    fun `should support copy functionality`() {
        val original = FormattingRulesUpdatedEvent(userId = "user123")
        val copied = original.copy(userId = "user456")

        assertEquals("user456", copied.userId)
    }
}
