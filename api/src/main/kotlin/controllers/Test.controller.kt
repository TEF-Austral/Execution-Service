package controllers

import component.AssetServiceClient
import dtos.CreateTestRequestDTO
import dtos.TestDTO
import dtos.TestExecutionResponseDTO
import dtos.UpdateTestRequestDTO
import entities.TestEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import repositories.TestRepository
import services.ExecutionService
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/tests")
class TestController(
    private val executionService: ExecutionService,
    private val assetServiceClient: AssetServiceClient,
    private val testRepository: TestRepository,
) {

    @PostMapping("/execute")
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
    fun createTest(
        @RequestBody request: CreateTestRequestDTO,
    ): ResponseEntity<TestDTO> {
        val test =
            TestEntity(
                snippetId = request.snippetId,
                name = request.name,
                inputs = request.inputs ?: emptyList(),
                expectedOutputs = request.expectedOutputs ?: emptyList(),
            )

        val saved = testRepository.save(test)

        return ResponseEntity.status(HttpStatus.CREATED).body(saved.toDTO())
    }

    @PutMapping("/{id}")
    fun updateTest(
        @PathVariable id: Long,
        @RequestBody request: UpdateTestRequestDTO,
    ): ResponseEntity<TestDTO> {
        val existingTest =
            testRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Test not found: $id") }

        val updatedTest =
            existingTest.copy(
                name = request.name ?: existingTest.name,
                inputs = request.inputs ?: existingTest.inputs,
                expectedOutputs = request.expectedOutputs ?: existingTest.expectedOutputs,
            )

        val saved = testRepository.save(updatedTest)
        return ResponseEntity.ok(saved.toDTO())
    }

    @GetMapping
    fun getTestsBySnippet(
        @RequestParam("snippetId") snippetId: Long,
    ): ResponseEntity<List<TestDTO>> {
        val tests = testRepository.findBySnippetId(snippetId)
        return ResponseEntity.ok(tests.map { it.toDTO() })
    }

    @GetMapping("/{id}")
    fun getTest(
        @PathVariable id: Long,
    ): ResponseEntity<TestDTO> {
        val test =
            testRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Test not found: $id") }
        return ResponseEntity.ok(test.toDTO())
    }

    @DeleteMapping("/{id}")
    fun deleteTest(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        testRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}

fun TestEntity.toDTO() =
    TestDTO(
        id = id,
        snippetId = snippetId,
        name = name,
        inputs = inputs,
        expectedOutputs = expectedOutputs,
    )
