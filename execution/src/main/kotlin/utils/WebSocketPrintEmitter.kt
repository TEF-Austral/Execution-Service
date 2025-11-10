package utils

import com.fasterxml.jackson.databind.ObjectMapper
import dtos.WebSocketMessage
import dtos.WebSocketMessageType
import emitter.Emitter
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import result.InterpreterResult

class WebSocketPrintEmitter(
    private val session: WebSocketSession,
    private val objectMapper: ObjectMapper,
) : Emitter {

    override fun emit(value: InterpreterResult) {
        val output = value.interpreter?.getValue().toString()
        sendOutput(output)
    }

    override fun stringEmit(value: String) {
        sendOutput(value)
    }

    private fun sendOutput(output: String) {
        try {
            if (session.isOpen) {
                val msg =
                    objectMapper.writeValueAsString(
                        WebSocketMessage(WebSocketMessageType.Output, value = output),
                    )
                session.sendMessage(TextMessage(msg))
            }
        } catch (e: Exception) {
            println("Error al emitir por WebSocket: ${e.message}")
        }
    }
}
