package utils

import emitter.Emitter
import result.InterpreterResult

class FakeEmitter : Emitter {

    override fun emit(value: InterpreterResult) {
    }

    override fun stringEmit(value: String) {
    }
}
