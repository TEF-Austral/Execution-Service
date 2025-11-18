package utils

import org.junit.jupiter.api.Test
import result.InterpreterResult
import type.CommonTypes
import variable.Variable

class FakeEmitterTest {

    @Test
    fun `emit should not throw exception`() {
        val emitter = FakeEmitter()
        val result = InterpreterResult(true, "success", Variable(CommonTypes.STRING, "test"))

        emitter.emit(result)
    }

    @Test
    fun `stringEmit should not throw exception`() {
        val emitter = FakeEmitter()

        emitter.stringEmit("test")
    }
}
