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
            val allTestResults =
                executionService.executeAllTests(
                    inputStream,
                    request.snippetId,
                    request.version,
                )

            allTestResults.executions.forEach { testResult ->
                val result =
                    TestingResultEvent(
                        requestId = request.requestId,
                        snippetId = request.snippetId,
                        passed = testResult.passed,
                        outputs = testResult.outputs,
                        expectedOutputs = testResult.expectedOutputs,
                        errors = testResult.errors,
                        testId = testResult.testId,
                    )
                resultProducer.emit(result)
            }

            println(
                "✅ [PrintScript] Testing completed: ${request.requestId} (${allTestResults.executions.size} tests)",
            )
        } catch (e: Exception) {
            println("❌ [PrintScript] Testing failed: ${e.message}")
        }
    }
}
