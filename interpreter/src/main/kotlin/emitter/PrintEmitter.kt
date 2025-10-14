package emitter

import result.InterpreterResult

class PrintEmitter : Emitter {
    override fun emit(value: InterpreterResult) {
        println(value.interpreter?.getValue())
    }

    override fun stringEmit(value: String) {
        println(value)
    }
}
