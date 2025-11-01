package utils

import input.InputProvider
import result.InterpreterResult
import type.CommonTypes
import variable.Variable

class InputProviderAdapter(
    private val provider: InputReceiver,
    private val emitter: PrintEmitter,
) : InputProvider {
    override fun input(name: String): InterpreterResult {
        emitter.print(name)
        val value = provider.input(name)
        return InterpreterResult(true, "Success", Variable(CommonTypes.STRING, value))
    }
}
