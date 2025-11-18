package events

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AnalyzerRulesUpdatedEventTest {

    @Test
    fun `should create AnalyzerRulesUpdatedEvent`() {
        val event = AnalyzerRulesUpdatedEvent(userId = "user123")

        assertEquals("user123", event.userId)
    }

    @Test
    fun `should support copy functionality`() {
        val original = AnalyzerRulesUpdatedEvent(userId = "user123")
        val copied = original.copy(userId = "user456")

        assertEquals("user456", copied.userId)
    }
}
