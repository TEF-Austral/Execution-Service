package utils

import com.fasterxml.jackson.databind.ObjectMapper
import dtos.WebSocketMessage
import dtos.WebSocketMessageType
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

class WebSocketErrorHandler(
    private val session: WebSocketSession,
    private val objectMapper: ObjectMapper,
) : ErrorHandler {

    override fun reportError(message: String?) {
        try {
            if (session.isOpen && message != null) {
                val msg =
                    objectMapper.writeValueAsString(
                        WebSocketMessage(WebSocketMessageType.Error, value = message),
                    )
                session.sendMessage(TextMessage(msg))
            }
        } catch (e: Exception) {
            println("Error al reportar error por WebSocket: ${e.message}")
        }
    }
}
