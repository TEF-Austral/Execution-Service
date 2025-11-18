package utils

import emitter.Emitter
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import result.InterpreterResult
import type.CommonTypes
import variable.Variable

class PrintEmitterAdapterTest {

    @Test
    fun `emit should delegate to printEmitter`() {
        val mockEmitter = mock(Emitter::class.java)
        val adapter = PrintEmitterAdapter(mockEmitter)
        val result = InterpreterResult(true, "message", Variable(CommonTypes.STRING, "test"))

        adapter.emit(result)

        verify(mockEmitter).emit(result)
    }

    @Test
    fun `stringEmit should delegate to printEmitter`() {
        val mockEmitter = mock(Emitter::class.java)
        val adapter = PrintEmitterAdapter(mockEmitter)
        val testString = "test message"

        adapter.stringEmit(testString)

        verify(mockEmitter).stringEmit(testString)
    }
}
