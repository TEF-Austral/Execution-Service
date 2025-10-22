

class ExecutionServiceTest {

//    private lateinit var repository: MockExecutionRepository
//    private lateinit var service: ExecutionService
//
//    @BeforeEach
//    fun setUp() {
//        repository = MockExecutionRepository()
//        service = ExecutionService(repository)
//    }
//
//    @Test
//    fun `createSnippet should save snippet and return DTO`() {
//        val request =
//            CreateSnippetRequest(
//                name = "Test Snippet",
//                content = "let x: number = 5;",
//                language = Language.PRINTSCRIPT,
//                version = "1.0",
//            )
//
//        val result = service.createSnippet(request)
//
//        assertEquals("Test Snippet", result.name)
//        assertEquals("let x: number = 5;", result.content)
//        assertEquals(Language.PRINTSCRIPT, result.language)
//        assertEquals("1.0", result.version)
//        assertNull(result.deletedAt)
//    }
//
//    @Test
//    fun `getAllSnippets should return all non-deleted snippets`() {
//        val snippet1 =
//            Snippet(
//                id = 1,
//                name = "Snippet 1",
//                content = "code1",
//                language = Language.PRINTSCRIPT,
//                version = "1.0",
//            )
//        val snippet2 =
//            Snippet(
//                id = 2,
//                name = "Snippet 2",
//                content = "code2",
//                language = Language.PRINTSCRIPT,
//                version = "1.1",
//            )
//        repository.saveSnippet(snippet1)
//        repository.saveSnippet(snippet2)
//
//        val result = service.getAllSnippets()
//
//        assertEquals(2, result.size)
//        assertEquals("Snippet 1", result[0].name)
//        assertEquals("Snippet 2", result[1].name)
//    }
//
//    @Test
//    fun `getSnippetById should return snippet if exists and not deleted`() {
//        val snippet =
//            Snippet(
//                id = 1,
//                name = "Test",
//                content = "code",
//                language = Language.PRINTSCRIPT,
//                version = "1.0",
//            )
//        repository.saveSnippet(snippet)
//
//        val result = service.getSnippetById(1)
//
//        assertNotNull(result)
//        assertEquals("Test", result?.name)
//    }
//
//    @Test
//    fun `getSnippetById should return null if snippet does not exist`() {
//        val result = service.getSnippetById(999)
//
//        assertNull(result)
//    }
//
//    @Test
//    fun `deleteSnippet should mark snippet as deleted`() {
//        val snippet =
//            Snippet(
//                id = 1,
//                name = "Test",
//                content = "code",
//                language = Language.PRINTSCRIPT,
//                version = "1.0",
//            )
//        repository.saveSnippet(snippet)
//
//        service.deleteSnippet(1)
//
//        val result = repository.findSnippetByIdNotDeleted(1)
//        assertNull(result)
//    }
//
//    @Test
//    fun `deleteSnippet should throw exception if snippet not found`() {
//        assertThrows(IllegalArgumentException::class.java) {
//            service.deleteSnippet(999)
//        }
//    }
//
//    @Test
//    fun `createTest should create test for existing snippet`() {
//        val snippet =
//            Snippet(
//                id = 1,
//                name = "Main",
//                content = "code",
//                language = Language.PRINTSCRIPT,
//                version = "1.0",
//            )
//        repository.saveSnippet(snippet)
//
//        val request =
//            CreateTestRequest(
//                mainCodeId = 1,
//                inputs = "input",
//                outputs = "output",
//                expectedOutcomeIsSuccess = true,
//            )
//
//        val result = service.createTest(request)
//
//        assertEquals(1, result.mainCodeId)
//        assertEquals("input", result.inputs)
//        assertEquals("output", result.outputs)
//        assertTrue(result.expectedOutcomeIsSuccess)
//    }
//
//    @Test
//    fun `createTest should throw exception if snippet not found`() {
//        val request =
//            CreateTestRequest(
//                mainCodeId = 999,
//                inputs = "input",
//                outputs = "output",
//                expectedOutcomeIsSuccess = true,
//            )
//
//        assertThrows(IllegalArgumentException::class.java) {
//            service.createTest(request)
//        }
//    }
//
//    @Test
//    fun `executeSnippet should return error if snippet not found`() {
//        val result = service.executeSnippet(999)
//
//        assertFalse(result.wasSuccessful)
//        assertEquals("Snippet not found", result.result)
//    }
//
//    @Test
//    fun `executeSnippet should execute printscript snippet`() {
//        val snippet =
//            Snippet(
//                id = 1,
//                name = "Test",
//                content = "let x: number = 5;",
//                language = Language.PRINTSCRIPT,
//                version = "1.0",
//            )
//        repository.saveSnippet(snippet)
//
//        val result = service.executeSnippet(1)
//
//        assertNotNull(result)
//    }
//
//    @Test
//    fun `getAllSnippets should not return deleted snippets`() {
//        val snippet1 =
//            Snippet(
//                id = 1,
//                name = "Active",
//                content = "code",
//                language = Language.PRINTSCRIPT,
//                version = "1.0",
//            )
//        val snippet2 =
//            Snippet(
//                id = 2,
//                name = "Deleted",
//                content = "code",
//                language = Language.PRINTSCRIPT,
//                version = "1.0",
//                deletedAt = LocalDateTime.now(),
//            )
//        repository.saveSnippet(snippet1)
//        repository.saveSnippet(snippet2)
//
//        val result = service.getAllSnippets()
//
//        assertEquals(1, result.size)
//        assertEquals("Active", result[0].name)
//    }
}
