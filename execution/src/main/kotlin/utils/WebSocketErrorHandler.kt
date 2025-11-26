package utils

import com.fasterxml.jackson.databind.ObjectMapper
import dtos.WebSocketMessageDTO
import dtos.WebSocketMessageType
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

class WebSocketErrorHandler(
    private val session: WebSocketSession,
    private val objectMapper: ObjectMapper,
) : ErrorHandler {

    private val log = org.slf4j.LoggerFactory.getLogger(WebSocketErrorHandler::class.java)

    override fun reportError(message: String?) {
        try {
            if (session.isOpen && message != null) {
                val msg =
                    objectMapper.writeValueAsString(
                        WebSocketMessageDTO(WebSocketMessageType.Error, value = message),
                    )
                session.sendMessage(TextMessage(msg))
            }
        } catch (e: Exception) {
            log.warn("Error reporting error via WebSocket: ${e.message}")
        }
    }
}
