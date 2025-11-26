package utils

import com.fasterxml.jackson.databind.ObjectMapper
import dtos.WebSocketMessageDTO
import dtos.WebSocketMessageType
import emitter.Emitter
import org.slf4j.LoggerFactory
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import result.InterpreterResult

class WebSocketPrintEmitter(
    private val session: WebSocketSession,
    private val objectMapper: ObjectMapper,
) : Emitter {

    private val log = LoggerFactory.getLogger(WebSocketPrintEmitter::class.java)

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
                        WebSocketMessageDTO(WebSocketMessageType.Output, value = output),
                    )
                session.sendMessage(TextMessage(msg))
            }
        } catch (e: Exception) {
            log.error("Error emitting via WebSocket: ${e.message}")
        }
    }
}
