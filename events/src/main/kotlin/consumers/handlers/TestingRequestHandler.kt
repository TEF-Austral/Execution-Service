package consumers.handlers

import TestingRequestEvent
import TestingResultEvent
import component.AssetServiceClient
import org.springframework.stereotype.Service
import producers.TestingResultProducer
import services.ExecutionService
import java.io.ByteArrayInputStream

@Service
class TestingRequestHandler(
    private val executionService: ExecutionService,
    private val assetServiceClient: AssetServiceClient,
    private val resultProducer: TestingResultProducer,
) {
    fun handle(request: TestingRequestEvent) {
        println("🧪 [PrintScript] Processing testing request: ${request.requestId}")

        try {
            val content =
                assetServiceClient.getAsset(
                    request.bucketContainer,
                    request.bucketKey,
                )

            val inputStream = ByteArrayInputStream(content.toByteArray())
            val testResult =
                executionService.executeTest(
                    inputStream,
                    request.version,
                    request.testId,
                )

            val result =
                TestingResultEvent(
                    requestId = request.requestId,
                    testId = request.testId,
                    snippetId = request.snippetId,
                    passed = testResult.passed,
                    outputs = testResult.outputs,
                    expectedOutputs = testResult.expectedOutputs,
                    errors = testResult.errors,
                )

            resultProducer.emit(result)
            println("✅ [PrintScript] Testing completed: ${request.requestId}")
        } catch (e: Exception) {
            println("❌ [PrintScript] Testing failed: ${e.message}")
        }
    }
}
