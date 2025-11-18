package events

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class EventsExtendedTest {

    @Test
    fun `FormattingRulesUpdatedEvent should be created with userId`() {
        val userId = "user-123"
        val event = FormattingRulesUpdatedEvent(userId)

        assertNotNull(event)
        assertEquals(userId, event.userId)
    }

    @Test
    fun `FormattingRulesUpdatedEvent should support multiple users`() {
        val event1 = FormattingRulesUpdatedEvent("user-1")
        val event2 = FormattingRulesUpdatedEvent("user-2")
        val event3 = FormattingRulesUpdatedEvent("user-3")

        assertEquals("user-1", event1.userId)
        assertEquals("user-2", event2.userId)
        assertEquals("user-3", event3.userId)
    }

    @Test
    fun `AnalyzerRulesUpdatedEvent should be created with userId`() {
        val userId = "user-456"
        val event = AnalyzerRulesUpdatedEvent(userId)

        assertNotNull(event)
        assertEquals(userId, event.userId)
    }

    @Test
    fun `AnalyzerRulesUpdatedEvent should support multiple users`() {
        val event1 = AnalyzerRulesUpdatedEvent("user-a")
        val event2 = AnalyzerRulesUpdatedEvent("user-b")
        val event3 = AnalyzerRulesUpdatedEvent("user-c")

        assertEquals("user-a", event1.userId)
        assertEquals("user-b", event2.userId)
        assertEquals("user-c", event3.userId)
    }

    @Test
    fun `FormattingRulesUpdatedEvent should support data class operations`() {
        val event1 = FormattingRulesUpdatedEvent("user-123")
        val event2 = FormattingRulesUpdatedEvent("user-123")
        val event3 = FormattingRulesUpdatedEvent("user-456")

        assertEquals(event1, event2)
        assertEquals(event1.hashCode(), event2.hashCode())
        assertEquals(event1.toString(), event2.toString())

        assertNotNull(event1.copy())
        assertEquals(event1, event1.copy())
    }

    @Test
    fun `AnalyzerRulesUpdatedEvent should support data class operations`() {
        val event1 = AnalyzerRulesUpdatedEvent("user-789")
        val event2 = AnalyzerRulesUpdatedEvent("user-789")
        val event3 = AnalyzerRulesUpdatedEvent("user-101")

        assertEquals(event1, event2)
        assertEquals(event1.hashCode(), event2.hashCode())
        assertEquals(event1.toString(), event2.toString())

        assertNotNull(event1.copy())
        assertEquals(event1, event1.copy())
    }

    @Test
    fun `FormattingRulesUpdatedEvent copy should allow changing userId`() {
        val event1 = FormattingRulesUpdatedEvent("original-user")
        val event2 = event1.copy(userId = "new-user")

        assertEquals("original-user", event1.userId)
        assertEquals("new-user", event2.userId)
    }

    @Test
    fun `AnalyzerRulesUpdatedEvent copy should allow changing userId`() {
        val event1 = AnalyzerRulesUpdatedEvent("original-user")
        val event2 = event1.copy(userId = "new-user")

        assertEquals("original-user", event1.userId)
        assertEquals("new-user", event2.userId)
    }

    @Test
    fun `Events should handle empty userId`() {
        val formattingEvent = FormattingRulesUpdatedEvent("")
        val analyzerEvent = AnalyzerRulesUpdatedEvent("")

        assertEquals("", formattingEvent.userId)
        assertEquals("", analyzerEvent.userId)
    }

    @Test
    fun `Events should handle special characters in userId`() {
        val specialUserId = "user@example.com|auth0|123-456-789"
        val formattingEvent = FormattingRulesUpdatedEvent(specialUserId)
        val analyzerEvent = AnalyzerRulesUpdatedEvent(specialUserId)

        assertEquals(specialUserId, formattingEvent.userId)
        assertEquals(specialUserId, analyzerEvent.userId)
    }
}
