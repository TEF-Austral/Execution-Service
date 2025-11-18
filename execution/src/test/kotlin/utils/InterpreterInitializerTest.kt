package utils

import emitter.Emitter
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import java.io.ByteArrayInputStream

class InterpreterInitializerTest {

    @Test
    fun `execute should run without errors for valid code`() {
        val code = "let x: number = 5;\nprintln(x);"
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val printEmitter = mock<Emitter>()
        val inputEmitter = mock<Emitter>()
        val errorHandler = mock<ErrorHandler>()
        val inputReceiver = mock<InputReceiver>()

        InterpreterInitializer.execute(
            inputStream,
            "1.0",
            printEmitter,
            inputEmitter,
            errorHandler,
            inputReceiver,
        )
    }

    @Test
    fun `execute should handle invalid code`() {
        val code = "invalid code"
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val printEmitter = mock<Emitter>()
        val inputEmitter = mock<Emitter>()
        val errorHandler = mock<ErrorHandler>()
        val inputReceiver = mock<InputReceiver>()

        InterpreterInitializer.execute(
            inputStream,
            "1.0",
            printEmitter,
            inputEmitter,
            errorHandler,
            inputReceiver,
        )
    }
}
