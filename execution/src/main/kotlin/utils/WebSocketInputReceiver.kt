package utils

import com.fasterxml.jackson.databind.ObjectMapper
import dtos.WebSocketMessageDTO
import dtos.WebSocketMessageType
import org.slf4j.LoggerFactory
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.BlockingQueue

class WebSocketInputReceiver(
    private val session: WebSocketSession,
    private val inputQueue: BlockingQueue<String>,
    private val objectMapper: ObjectMapper,
) : InputReceiver {

    private val log = LoggerFactory.getLogger(WebSocketInputReceiver::class.java)

    override fun input(name: String?): String? {
        try {
            val requestMsg =
                objectMapper.writeValueAsString(
                    WebSocketMessageDTO(
                        WebSocketMessageType.InputRequest,
                        prompt = name ?: "Entrada:",
                    ),
                )
            session.sendMessage(TextMessage(requestMsg))
            Thread.sleep(100)

            return inputQueue.take()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            log.warn("InputReceiver thread interrupted: ${e.message}")
            return null
        } catch (e: Exception) {
            log.error("Error requesting input via WebSocket: ${e.message}")
            return null
        }
    }
}
