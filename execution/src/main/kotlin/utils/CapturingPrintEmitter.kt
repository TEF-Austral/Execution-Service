package utils

import emitter.Emitter
import result.InterpreterResult

class CapturingPrintEmitter(
    private val outputs: MutableList<String>,
) : Emitter {

    override fun emit(value: InterpreterResult) {
        outputs.add(value.interpreter?.getValue().toString())
    }

    override fun stringEmit(value: String) {
        outputs.add(value)
    }
}
