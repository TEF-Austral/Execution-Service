package input

import result.InterpreterResult
import type.CommonTypes
import variable.Variable

class EnvInputProvider : InputProvider {

    override fun input(name: String): InterpreterResult {
        return try {
            val value =
                System.getenv(name)
                    ?: return InterpreterResult(false, "Environment variable $name not found", null)
            InterpreterResult(
                true,
                "Successfully read environment variable '$name'.",
                Variable(CommonTypes.STRING_LITERAL, value),
            )
        } catch (e: Exception) {
            InterpreterResult(
                false,
                "Error retrieving environment variable $name: ${e.message}",
                null,
            )
        }
    }
}
