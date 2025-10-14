package emitter

import result.InterpreterResult

interface Emitter {
    fun emit(value: InterpreterResult)

    fun stringEmit(value: String)
}
