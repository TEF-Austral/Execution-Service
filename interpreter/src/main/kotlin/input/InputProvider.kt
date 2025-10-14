package input

import result.InterpreterResult

interface InputProvider {
    fun input(name: String): InterpreterResult
}
