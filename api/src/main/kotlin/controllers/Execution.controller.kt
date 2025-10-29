package api.controllers

import api.services.ExecutionService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/execution")
class ExecutionController(
    private val executionService: ExecutionService,
) {

//    @PostMapping("/snippets")
//    fun createSnippet(
//        @RequestBody request: CreateSnippetRequest,
//    ): ResponseEntity<SnippetDTO> {
//        val created = executionService.createSnippet(request)
//        return ResponseEntity.status(HttpStatus.CREATED).body(created)
//    }
//
//    @GetMapping("/snippets")
//    fun getAllSnippets(): ResponseEntity<List<SnippetDTO>> {
//        val snippets = executionService.getAllSnippets()
//        return ResponseEntity.ok(snippets)
//    }
//
//    @GetMapping("/snippets/{id}")
//    fun getSnippetById(
//        @PathVariable id: Long,
//    ): ResponseEntity<SnippetDTO> {
//        val snippet =
//            executionService.getSnippetById(id)
//                ?: return ResponseEntity.notFound().build()
//        return ResponseEntity.ok(snippet)
//    }
//
//    @DeleteMapping("/snippets/{id}")
//    fun deleteSnippet(
//        @PathVariable id: Long,
//    ): ResponseEntity<Void> =
//        try {
//            executionService.deleteSnippet(id)
//            ResponseEntity.noContent().build()
//        } catch (e: IllegalArgumentException) {
//            ResponseEntity.notFound().build()
//        }
//
//    @PostMapping("/tests")
//    fun createTest(
//        @RequestBody request: CreateTestRequest,
//    ): ResponseEntity<TestDTO> =
//        try {
//            val test = executionService.createTest(request)
//            ResponseEntity.status(HttpStatus.CREATED).body(test)
//        } catch (e: IllegalArgumentException) {
//            ResponseEntity.badRequest().build()
//        }
//
//    @PostMapping("/execute")
//    fun executeSnippet(
//        @RequestBody request: ExecuteSnippetRequest,
//    ): ResponseEntity<ExecutionResult> {
//        val result = executionService.executeSnippet(request.snippetId)
//        return ResponseEntity.ok(result)
//    }
}
