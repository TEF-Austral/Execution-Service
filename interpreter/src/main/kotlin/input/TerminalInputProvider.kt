package input

import result.InterpreterResult
import type.CommonTypes
import variable.Variable

class TerminalInputProvider : InputProvider {

    override fun input(name: String): InterpreterResult {
        print(name)
        val value = readln()
        return InterpreterResult(true, "Successful", Variable(CommonTypes.STRING_LITERAL, value))
    }
}
