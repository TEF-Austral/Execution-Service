package controllers

import Language
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
import services.LanguagesExecutionService
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/tests")
class TestController(
    private val languagesExecutionService: LanguagesExecutionService,
    private val assetServiceClient: AssetServiceClient,
    private val testRepository: TestRepository,
) {
    private val log = org.slf4j.LoggerFactory.getLogger(TestController::class.java)

    @PostMapping("/execute")
    fun executeTest(
        @RequestParam("container") container: String,
        @RequestParam("key") key: String,
        @RequestParam("version") version: String,
        @RequestParam("testId") testId: Long,
        @RequestParam("language") language: Language,
    ): ResponseEntity<TestExecutionResponseDTO> {
        log.info(
            "POST /tests/execute - Executing test $testId, version $version, language $language",
        )
        val assetContent = assetServiceClient.getAsset(container, key)
        val inputStream = ByteArrayInputStream(assetContent.toByteArray(StandardCharsets.UTF_8))
        val result = languagesExecutionService.executeTest(inputStream, version, testId, language)
        log.warn("POST /tests/execute - Test $testId executed, passed: ${result.passed}")
        return ResponseEntity.ok(result)
    }

    @PostMapping
    fun createTest(
        @RequestBody request: CreateTestRequestDTO,
    ): ResponseEntity<TestDTO> {
        log.info("POST /tests - Creating test for snippet ${request.snippetId}")
        val test =
            TestEntity(
                snippetId = request.snippetId,
                name = request.name,
                inputs = request.inputs ?: emptyList(),
                expectedOutputs = request.expectedOutputs ?: emptyList(),
            )
        val saved = testRepository.save(test)
        log.warn("POST /tests - Test created with id ${saved.id}")
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.toDTO())
    }

    @PutMapping("/{id}")
    fun updateTest(
        @PathVariable id: Long,
        @RequestBody request: UpdateTestRequestDTO,
    ): ResponseEntity<TestDTO> {
        log.info("PUT /tests/$id - Updating test")
        val existingTest =
            testRepository.findById(id).orElseThrow {
                NoSuchElementException("Test not found: $id")
            }
        val updatedTest =
            existingTest.copy(
                name = request.name ?: existingTest.name,
                inputs = request.inputs ?: existingTest.inputs,
                expectedOutputs = request.expectedOutputs ?: existingTest.expectedOutputs,
            )
        val saved = testRepository.save(updatedTest)
        log.warn("PUT /tests/$id - Test updated successfully")
        return ResponseEntity.ok(saved.toDTO())
    }

    @GetMapping
    fun getTestsBySnippet(
        @RequestParam("snippetId") snippetId: Long,
    ): ResponseEntity<List<TestDTO>> {
        log.info("GET /tests - Fetching tests for snippet $snippetId")
        val tests = testRepository.findBySnippetId(snippetId)
        log.warn("GET /tests - Retrieved ${tests.size} tests")
        return ResponseEntity.ok(tests.map { it.toDTO() })
    }

    @GetMapping("/{id}")
    fun getTest(
        @PathVariable id: Long,
    ): ResponseEntity<TestDTO> {
        log.info("GET /tests/$id - Fetching test")
        val test =
            testRepository
                .findById(
                    id,
                ).orElseThrow { NoSuchElementException("Test not found: $id") }
        log.warn("GET /tests/$id - Test retrieved successfully")
        return ResponseEntity.ok(test.toDTO())
    }

    @DeleteMapping("/{id}")
    fun deleteTest(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        log.info("DELETE /tests/$id - Deleting test")
        testRepository.deleteById(id)
        log.warn("DELETE /tests/$id - Test deleted successfully")
        return ResponseEntity.noContent().build()
    }

    private fun TestEntity.toDTO() =
        TestDTO(
            id = id,
            snippetId = snippetId,
            name = name,
            inputs = inputs,
            expectedOutputs = expectedOutputs,
        )
}
