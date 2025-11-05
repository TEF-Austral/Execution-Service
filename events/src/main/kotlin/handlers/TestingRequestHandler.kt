package handlers

import TestingRequestEvent
import TestingResultEvent
import component.AssetServiceClient
import org.springframework.stereotype.Service
import producers.AsyncTaskResultProducer
import services.ExecutionService
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@Service
class TestingRequestHandler(
    private val assetServiceClient: AssetServiceClient,
    private val executionService: ExecutionService,
    private val resultProducer: AsyncTaskResultProducer,
) {

    fun handleTestingRequest(request: TestingRequestEvent) {
        println("🔨 [PrintScript Service] Processing testing request: ${request.requestId}")

        try {
            val content = assetServiceClient.getAsset(request.bucketContainer, request.bucketKey)
            val inputStream = ByteArrayInputStream(content.toByteArray(StandardCharsets.UTF_8))

            val result = executionService.executeTest(inputStream, request.version, request.testId)

            resultProducer.publishTestingResult(
                TestingResultEvent(
                    requestId = request.requestId,
                    testId = request.testId,
                    snippetId = request.snippetId,
                    passed = result.passed,
                    outputs = result.outputs,
                    expectedOutputs = result.expectedOutputs,
                    errors = result.errors,
                ),
            )

            println("✅ [PrintScript Service] Testing completed for request: ${request.requestId}")
        } catch (e: Exception) {
            println(
                "❌ [PrintScript Service] Testing failed for request: ${request.requestId}: ${e.message}",
            )

            resultProducer.publishTestingResult(
                TestingResultEvent(
                    requestId = request.requestId,
                    testId = request.testId,
                    snippetId = request.snippetId,
                    passed = false,
                    outputs = emptyList(),
                    expectedOutputs = emptyList(),
                    errors = listOf(e.message ?: "Unknown error"),
                ),
            )
        }
    }
}
