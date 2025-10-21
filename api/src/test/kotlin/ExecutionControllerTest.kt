import api.controllers.ExecutionController
import api.dtos.CreateSnippetRequest
import api.dtos.CreateTestRequest
import api.dtos.ExecuteSnippetRequest
import api.entities.Language
import api.repositories.MockExecutionRepository
import api.services.ExecutionService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ExecutionControllerTest {

    private lateinit var executionService: ExecutionService
    private lateinit var controller: ExecutionController

    @BeforeEach
    fun setUp() {
        executionService = ExecutionService(MockExecutionRepository())
        controller = ExecutionController(executionService)
    }

    @Test
    fun `createSnippet should return created snippet with status 201`() {
        val request =
            CreateSnippetRequest(
                name = "Test",
                content = "code",
                language = Language.PRINTSCRIPT,
                version = "1.0",
            )

        val response = controller.createSnippet(request)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals("Test", response.body?.name)
        assertEquals("code", response.body?.content)
    }

    @Test
    fun `getAllSnippets should return list of snippets with status 200`() {
        executionService.createSnippet(
            CreateSnippetRequest("Test1", "code1", Language.PRINTSCRIPT, "1.0"),
        )
        executionService.createSnippet(
            CreateSnippetRequest("Test2", "code2", Language.PRINTSCRIPT, "1.1"),
        )

        val response = controller.getAllSnippets()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(2, response.body?.size)
    }

    @Test
    fun `getSnippetById should return snippet with status 200 when found`() {
        controller.createSnippet(
            CreateSnippetRequest("Test", "code", Language.PRINTSCRIPT, "1.0"),
        )

        val response = controller.getSnippetById(1)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Test", response.body?.name)
    }

    @Test
    fun `getSnippetById should return 404 when snippet not found`() {
        val response = controller.getSnippetById(999)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `deleteSnippet should return 204 when successful`() {
        controller.createSnippet(
            CreateSnippetRequest("Test", "code", Language.PRINTSCRIPT, "1.0"),
        )

        val response = controller.deleteSnippet(1)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `deleteSnippet should return 404 when snippet not found`() {
        val response = controller.deleteSnippet(999)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `createTest should return created test with status 201`() {
        controller.createSnippet(
            CreateSnippetRequest("Main", "code", Language.PRINTSCRIPT, "1.0"),
        )
        val request =
            CreateTestRequest(
                mainCodeId = 1,
                inputs = "input",
                outputs = "output",
                expectedOutcomeIsSuccess = true,
            )

        val response = controller.createTest(request)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals("input", response.body?.inputs)
        assertEquals("output", response.body?.outputs)
    }

    @Test
    fun `createTest should return 400 when snippet not found`() {
        val request =
            CreateTestRequest(
                mainCodeId = 999,
                inputs = "input",
                outputs = "output",
                expectedOutcomeIsSuccess = true,
            )

        val response = controller.createTest(request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `executeSnippet should return execution result with status 200`() {
        controller.createSnippet(
            CreateSnippetRequest("Test", "let x: number = 5;", Language.PRINTSCRIPT, "1.0"),
        )
        val request = ExecuteSnippetRequest(snippetId = 1)

        val response = controller.executeSnippet(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
    }

    @Test
    fun `executeSnippet should return failed result when snippet not found`() {
        val request = ExecuteSnippetRequest(snippetId = 999)

        val response = controller.executeSnippet(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(false, response.body?.wasSuccessful)
    }
}
