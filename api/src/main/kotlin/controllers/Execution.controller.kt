package api.controllers

import api.dtos.CreateSnippetRequest
import api.dtos.CreateTestRequest
import api.dtos.ExecuteSnippetRequest
import api.dtos.ExecutionResult
import api.dtos.SnippetDTO
import api.dtos.TestDTO
import api.services.ExecutionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/execution")
class ExecutionController(
    private val executionService: ExecutionService,
) {

    @PostMapping("/snippets")
    fun createSnippet(
        @RequestBody request: CreateSnippetRequest,
    ): ResponseEntity<SnippetDTO> {
        val created = executionService.createSnippet(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping("/snippets")
    fun getAllSnippets(): ResponseEntity<List<SnippetDTO>> {
        val snippets = executionService.getAllSnippets()
        return ResponseEntity.ok(snippets)
    }

    @GetMapping("/snippets/{id}")
    fun getSnippetById(
        @PathVariable id: Long,
    ): ResponseEntity<SnippetDTO> {
        val snippet =
            executionService.getSnippetById(id)
                ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(snippet)
    }

    @DeleteMapping("/snippets/{id}")
    fun deleteSnippet(
        @PathVariable id: Long,
    ): ResponseEntity<Void> =
        try {
            executionService.deleteSnippet(id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }

    @PostMapping("/tests")
    fun createTest(
        @RequestBody request: CreateTestRequest,
    ): ResponseEntity<TestDTO> =
        try {
            val test = executionService.createTest(request)
            ResponseEntity.status(HttpStatus.CREATED).body(test)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    @PostMapping("/execute")
    fun executeSnippet(
        @RequestBody request: ExecuteSnippetRequest,
    ): ResponseEntity<ExecutionResult> {
        val result = executionService.executeSnippet(request.snippetId)
        return ResponseEntity.ok(result)
    }
}
