package api.controllers

import component.AssetServiceClient
import dtos.ExecutionResponseDTO
import dtos.InteractiveExecutionRequestDTO
import dtos.TestExecutionResponseDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import services.ExecutionService
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/execute")
class ExecutionController(
    private val executionService: ExecutionService,
    private val assetServiceClient: AssetServiceClient,
) {

    @PostMapping("/test")
    fun executeTest(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
        @RequestParam("testId") testId: Long,
    ): ResponseEntity<TestExecutionResponseDTO> {
        val assetContent = assetServiceClient.getAsset(container, key)
        val inputStream = ByteArrayInputStream(assetContent.toByteArray(StandardCharsets.UTF_8))

        val result = executionService.executeTest(inputStream, version, testId)

        return ResponseEntity.ok(result)
    }

    @PostMapping
    fun executeInteractive(
        @RequestBody request: InteractiveExecutionRequestDTO,
    ): ResponseEntity<ExecutionResponseDTO> {
        val assetContent = assetServiceClient.getAsset(request.container, request.key)
        val inputStream = ByteArrayInputStream(assetContent.toByteArray(StandardCharsets.UTF_8))

        val result =
            executionService.execute(
                inputStream,
                request.version,
                request.inputs,
            )

        return ResponseEntity.ok(result)
    }
}
