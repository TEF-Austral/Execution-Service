package controllers

import dtos.CreateTestRequestDTO
import dtos.TestDTO
import entities.TestEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import repositories.TestRepository

@RestController
@RequestMapping("/tests")
class TestController(
    private val testRepository: TestRepository,
) {

    @PostMapping
    fun createTest(
        @RequestBody request: CreateTestRequestDTO,
    ): ResponseEntity<TestDTO> {
        val test =
            TestEntity(
                snippetId = request.snippetId,
                name = request.name,
                inputs = request.inputs,
                expectedOutputs = request.expectedOutputs,
            )

        val saved = testRepository.save(test)

        return ResponseEntity.status(HttpStatus.CREATED).body(saved.toDTO())
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
