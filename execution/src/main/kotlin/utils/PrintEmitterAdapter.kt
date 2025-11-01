package utils

import emitter.Emitter
import result.InterpreterResult

class PrintEmitterAdapter(
    private val printEmitter: PrintEmitter,
) : Emitter {
    override fun emit(value: InterpreterResult) {
        printEmitter.print(value.interpreter?.getValue().toString())
    }

    override fun stringEmit(value: String) {
        printEmitter.print(value)
    }
}
