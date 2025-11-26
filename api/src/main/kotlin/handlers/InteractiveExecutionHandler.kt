package api.handlers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import component.AssetServiceClient
import dtos.WebSocketMessageDTO
import dtos.WebSocketMessageType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import utils.InterpreterInitializer
import utils.WebSocketErrorHandler
import utils.WebSocketInputReceiver
import utils.WebSocketPrintEmitter
import java.io.InputStream
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

@Component
class InteractiveExecutionHandler(
    private val assetServiceClient: AssetServiceClient,
) : TextWebSocketHandler() {

    private val log = LoggerFactory.getLogger(InteractiveExecutionHandler::class.java)
    private val objectMapper = jacksonObjectMapper()
    private val sessions = mutableMapOf<String, BlockingQueue<String>>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val inputQueue = LinkedBlockingQueue<String>(1)
        sessions[session.id] = inputQueue
        log.info("WebSocket session ${session.id} established. Waiting for initialization...")
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage,
    ) {
        val msg = objectMapper.readValue<WebSocketMessageDTO>(message.payload)

        when (msg.type) {
            WebSocketMessageType.InitExecution -> {
                if (msg.bucketContainer == null || msg.bucketKey == null || msg.version == null) {
                    session.close(
                        CloseStatus.BAD_DATA.withReason(
                            "Missing initialization data (bucket, key or version)",
                        ),
                    )
                    return
                }

                log.info(
                    "Received init_execution for session ${session.id}. Starting execution thread...",
                )

                val inputQueue = sessions[session.id]
                if (inputQueue == null) {
                    session.close(
                        CloseStatus.SERVER_ERROR.withReason(
                            "Input queue for session not found",
                        ),
                    )
                    return
                }

                val bucketContainer = msg.bucketContainer!!
                val bucketKey = msg.bucketKey!!
                val version = msg.version!!

                startExecutionThread(
                    session,
                    inputQueue,
                    bucketContainer,
                    bucketKey,
                    version,
                )
            }

            WebSocketMessageType.InputResponse -> {
                sessions[session.id]?.put(msg.value ?: "")
            }

            else -> {
                log.warn("Received unhandled message type: ${msg.type}")
            }
        }
    }

    private fun startExecutionThread(
        session: WebSocketSession,
        inputQueue: BlockingQueue<String>,
        bucketContainer: String,
        bucketKey: String,
        version: String,
    ) {
        thread {
            try {
                val printEmitter = WebSocketPrintEmitter(session, objectMapper)
                val inputReceiver = WebSocketInputReceiver(session, inputQueue, objectMapper)
                val errorHandler = WebSocketErrorHandler(session, objectMapper)

                val snippetContent: String = assetServiceClient.getAsset(bucketContainer, bucketKey)
                val snippetContentStream: InputStream =
                    snippetContent.byteInputStream(
                        Charsets.UTF_8,
                    )

                InterpreterInitializer.execute(
                    snippetContentStream,
                    version,
                    printEmitter,
                    printEmitter,
                    errorHandler,
                    inputReceiver,
                )

                val finishedMsg =
                    objectMapper.writeValueAsString(
                        WebSocketMessageDTO(WebSocketMessageType.ExecutionFinished),
                    )
                session.sendMessage(TextMessage(finishedMsg))
            } catch (e: Exception) {
                val errorMsg =
                    objectMapper.writeValueAsString(
                        WebSocketMessageDTO(WebSocketMessageType.Error, value = e.message),
                    )
                session.sendMessage(TextMessage(errorMsg))
            } finally {
                if (session.isOpen) {
                    session.close()
                }
            }
        }
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus,
    ) {
        sessions.remove(session.id)
        log.info("WebSocket session ${session.id} closed: ${status.reason}")
    }
}
