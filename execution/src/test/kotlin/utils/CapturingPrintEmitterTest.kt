package utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import result.InterpreterResult
import type.CommonTypes
import variable.Variable

class CapturingPrintEmitterTest {

    @Test
    fun `emit should capture output value`() {
        val outputs = mutableListOf<String>()
        val emitter = CapturingPrintEmitter(outputs)
        val result = InterpreterResult(true, "message", Variable(CommonTypes.STRING, "captured"))

        emitter.emit(result)

        assertEquals(1, outputs.size)
        assertEquals("captured", outputs[0])
    }

    @Test
    fun `stringEmit should capture string value`() {
        val outputs = mutableListOf<String>()
        val emitter = CapturingPrintEmitter(outputs)

        emitter.stringEmit("test output")

        assertEquals(1, outputs.size)
        assertEquals("test output", outputs[0])
    }

    @Test
    fun `multiple emissions should be captured in order`() {
        val outputs = mutableListOf<String>()
        val emitter = CapturingPrintEmitter(outputs)

        emitter.stringEmit("first")
        emitter.stringEmit("second")
        val result = InterpreterResult(true, "msg", Variable(CommonTypes.NUMBER, 42))
        emitter.emit(result)

        assertEquals(3, outputs.size)
        assertEquals("first", outputs[0])
        assertEquals("second", outputs[1])
        assertEquals("42", outputs[2])
    }

    @Test
    fun `emit with null interpreter should capture null toString`() {
        val outputs = mutableListOf<String>()
        val emitter = CapturingPrintEmitter(outputs)
        val result = InterpreterResult(false, "error", null)

        emitter.emit(result)

        assertEquals(1, outputs.size)
        assertEquals("null", outputs[0])
    }
}
