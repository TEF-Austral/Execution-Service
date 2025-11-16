package consumers.handlers

import component.AssetService
import dtos.responses.TestingResultEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import producers.TestingResultProducer
import repositories.TestRepository
import requests.TestingRequestEvent
import services.PrintScriptExecutionService

@Service
class TestingRequestHandler(
    private val assetService: AssetService,
    private val testRepository: TestRepository,
    private val executionService: PrintScriptExecutionService,
    private val testingResultProducer: TestingResultProducer,
) : ITestingRequestHandler {

    private val log = LoggerFactory.getLogger(TestingRequestHandler::class.java)

    override fun handle(request: TestingRequestEvent) {
        try {
            log.info(
                "Procesando solicitud de testing: requestId=${request.requestId}, snippetId=${request.snippetId}",
            )

            val content =
                assetService.getAsset(
                    container = request.bucketContainer,
                    key = request.bucketKey,
                )

            val tests = testRepository.findBySnippetId(request.snippetId)

            if (tests.isEmpty()) {
                log.info("No se encontraron tests para el snippet ${request.snippetId}")
                return
            }

            log.info(
                "Encontrados ${tests.size} tests para snippet ${request.snippetId}. Ejecutando...",
            )

            tests.forEach { test ->
                try {
                    val inputStream = content.byteInputStream()
                    val result =
                        executionService.executeTest(
                            inputStream = inputStream,
                            version = request.version,
                            testId = test.id,
                        )

                    val resultEvent =
                        TestingResultEvent(
                            requestId = request.requestId,
                            testId = result.testId,
                            snippetId = request.snippetId,
                            passed = result.passed,
                            outputs = result.outputs,
                            expectedOutputs = result.expectedOutputs,
                            errors = result.errors,
                        )
                    testingResultProducer.emit(resultEvent)
                    log.info(
                        "Test ejecutado: testId=${test.id}, passed=${result.passed}, snippetId=${request.snippetId}",
                    )
                } catch (e: Exception) {
                    log.error(
                        "Error ejecutando test ${test.id} para snippet ${request.snippetId}: ${e.message}",
                        e,
                    )
                    val errorResult =
                        TestingResultEvent(
                            requestId = request.requestId,
                            testId = test.id,
                            snippetId = request.snippetId,
                            passed = false,
                            outputs = emptyList(),
                            expectedOutputs = test.expectedOutputs,
                            errors = listOf(e.message ?: "Unknown error"),
                        )
                    testingResultProducer.emit(errorResult)
                }
            }
            log.info(
                "Testing completado para snippet ${request.snippetId}: ${tests.size} tests ejecutados.",
            )
        } catch (e: Exception) {
            log.error("Error fatal procesando testing request: ${e.message}", e)
        }
    }
}
