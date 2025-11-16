package consumers.handlers

import component.AssetService
import dtos.responses.TestingResultEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import producers.TestingResultProducer
import repositories.TestRepository
import requests.TestingRequestEvent
import services.PrintScriptExecutionService

@Component
class TestingRequestHandler(
    private val assetService: AssetService,
    private val executionService: PrintScriptExecutionService,
    private val testRepository: TestRepository,
    private val testingResultProducer: TestingResultProducer,
) : ITestingRequestHandler {

    private val log = LoggerFactory.getLogger(TestingRequestHandler::class.java)

    override fun handle(request: TestingRequestEvent) {
        try {
            log.info(
                "Processing testing request: requestId=${request.requestId}, snippetId=${request.snippetId}",
            )

            // Get snippet content from asset service
            val content =
                assetService.getAsset(
                    container = request.bucketContainer,
                    key = request.bucketKey,
                )

            // Get all tests for this snippet
            val tests = testRepository.findBySnippetId(request.snippetId)

            if (tests.isEmpty()) {
                log.info("No tests found for snippet ${request.snippetId}")
                return
            }

            log.info("Found ${tests.size} tests for snippet ${request.snippetId}")

            // Execute each test and publish result
            tests.forEach { test ->
                try {
                    val inputStream = content.byteInputStream()
                    val result =
                        executionService.executeTest(
                            inputStream = inputStream,
                            version = request.version,
                            testId = test.id,
                        )

                    // Emit result event
                    val resultEvent =
                        TestingResultEvent(
                            requestId = request.requestId,
                            testId = test.id,
                            snippetId = request.snippetId,
                            passed = result.passed,
                            outputs = result.outputs,
                            expectedOutputs = result.expectedOutputs,
                            errors = result.errors,
                        )

                    testingResultProducer.emit(resultEvent)

                    log.info(
                        "Test executed: testId=${test.id}, passed=${result.passed}, snippetId=${request.snippetId}",
                    )
                } catch (e: Exception) {
                    log.error(
                        "Error executing test ${test.id} for snippet ${request.snippetId}: ${e.message}",
                        e,
                    )

                    // Emit error result
                    val errorEvent =
                        TestingResultEvent(
                            requestId = request.requestId,
                            testId = test.id,
                            snippetId = request.snippetId,
                            passed = false,
                            outputs = emptyList(),
                            expectedOutputs = test.expectedOutputs,
                            errors = listOf(e.message ?: "Unknown error"),
                        )
                    testingResultProducer.emit(errorEvent)
                }
            }

            log.info(
                "Completed testing for snippet ${request.snippetId}: ${tests.size} tests executed",
            )
        } catch (e: Exception) {
            log.error("Error processing testing request: ${e.message}", e)
        }
    }
}
