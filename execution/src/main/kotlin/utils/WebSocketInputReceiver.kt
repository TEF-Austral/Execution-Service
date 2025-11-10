package utils

import com.fasterxml.jackson.databind.ObjectMapper
import dtos.WebSocketMessage
import dtos.WebSocketMessageType
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.BlockingQueue

class WebSocketInputReceiver(
    private val session: WebSocketSession,
    private val inputQueue: BlockingQueue<String>,
    private val objectMapper: ObjectMapper,
) : InputReceiver {

    override fun input(name: String?): String? {
        try {
            // 1. Pide la entrada al cliente
            // CAMBIO: Usar el enum
            val requestMsg =
                objectMapper.writeValueAsString(
                    WebSocketMessage(
                        WebSocketMessageType.InputRequest,
                        prompt = name ?: "Entrada:",
                    ),
                )
            session.sendMessage(TextMessage(requestMsg))

            return inputQueue.take()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            println("Hilo de InputReceiver interrumpido: ${e.message}")
            return null
        } catch (e: Exception) {
            println("Error al pedir entrada por WebSocket: ${e.message}")
            return null
        }
    }
}
