package handlers

import component.AssetServiceClient
import consumers.handlers.ITestingRequestHandler
import dtos.AllTestSnippetExecution
import org.springframework.stereotype.Service
import producers.TestingResultProducer
import requests.TestingRequestEvent
import results.TestingResultEvent
import services.ExecutionService
import java.io.ByteArrayInputStream

@Service
class TestingRequestHandler(
    private val executionService: ExecutionService,
    private val assetServiceClient: AssetServiceClient,
    private val resultProducer: TestingResultProducer,
) : ITestingRequestHandler {
    override fun handle(request: TestingRequestEvent) {
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

            emitTestingResults(allTestResults, request)
        } catch (e: Exception) {
            println("[PrintScript] Testing failed: ${e.message}")
        }
    }

    private fun emitTestingResults(
        allTestResults: AllTestSnippetExecution,
        request: TestingRequestEvent,
    ) {
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
    }
}
