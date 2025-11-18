package utils

import emitter.Emitter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class InputProviderAdapterTest {

    @Test
    fun `input should call provider and emitter`() {
        val provider = mock<InputReceiver>()
        val emitter = mock<Emitter>()
        whenever(provider.input(any())).thenReturn("test value")

        val adapter = InputProviderAdapter(provider, emitter)

        val result = adapter.input("Enter name")

        verify(emitter).stringEmit("Enter name")
        verify(provider).input("Enter name")
        assertEquals("test value", result.interpreter?.getValue())
    }

    @Test
    fun `input should handle null value from provider`() {
        val provider = mock<InputReceiver>()
        val emitter = mock<Emitter>()
        whenever(provider.input(any())).thenReturn(null)

        val adapter = InputProviderAdapter(provider, emitter)

        val result = adapter.input("prompt")

        assertEquals(null, result.interpreter?.getValue())
    }
}
