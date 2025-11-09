package utils

import emitter.Emitter
import input.InputProvider
import result.InterpreterResult
import type.CommonTypes
import variable.Variable

class InputProviderAdapter(
    private val provider: InputReceiver,
    private val emitter: Emitter,
) : InputProvider {
    override fun input(name: String): InterpreterResult {
        emitter.stringEmit(name)
        val value = provider.input(name)
        return InterpreterResult(true, "Success", Variable(CommonTypes.STRING, value))
    }
}
