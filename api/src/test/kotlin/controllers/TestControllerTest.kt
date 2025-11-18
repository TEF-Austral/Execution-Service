package controllers

import Language
import component.AssetServiceClient
import dtos.CreateTestRequestDTO
import dtos.TestExecutionResponseDTO
import dtos.UpdateTestRequestDTO
import entities.TestEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import repositories.TestRepository
import services.LanguagesExecutionService
import java.io.InputStream
import java.util.Optional

class TestControllerTest {

    private lateinit var languagesExecutionService: LanguagesExecutionService
    private lateinit var assetServiceClient: AssetServiceClient
    private lateinit var testRepository: TestRepository
    private lateinit var controller: TestController

    @BeforeEach
    fun setup() {
        languagesExecutionService = mockk(relaxed = true)
        assetServiceClient = mockk(relaxed = true)
        testRepository = mockk(relaxed = true)
        controller =
            TestController(
                languagesExecutionService,
                assetServiceClient,
                testRepository,
            )
    }

    @Test
    fun `executeTest should execute test and return result`() {
        val container = "test-container"
        val key = "test-key"
        val version = "1.0"
        val testId = 1L
        val language = Language.PRINTSCRIPT
        val assetContent = "println(42);"
        val testResult =
            TestExecutionResponseDTO(testId, true, listOf("42"), listOf("42"), emptyList())

        every { assetServiceClient.getAsset(container, key) } returns assetContent
        every {
            languagesExecutionService.executeTest(any<InputStream>(), version, testId, language)
        } returns testResult

        val response = controller.executeTest(container, key, version, testId, language)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(testId, response.body!!.testId)
        assertEquals(true, response.body!!.passed)

        verify { assetServiceClient.getAsset(container, key) }
        verify {
            languagesExecutionService.executeTest(
                any<InputStream>(),
                version,
                testId,
                language,
            )
        }
    }

    @Test
    fun `executeTest should handle failed test execution`() {
        val container = "test-container"
        val key = "test-key"
        val version = "1.0"
        val testId = 1L
        val language = Language.PRINTSCRIPT
        val assetContent = "println(42);"
        val testResult =
            TestExecutionResponseDTO(
                testId,
                false,
                listOf("42"),
                listOf("100"),
                listOf("Output mismatch"),
            )

        every { assetServiceClient.getAsset(container, key) } returns assetContent
        every {
            languagesExecutionService.executeTest(any<InputStream>(), version, testId, language)
        } returns testResult

        val response = controller.executeTest(container, key, version, testId, language)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(false, response.body!!.passed)
        assertEquals(listOf("Output mismatch"), response.body!!.errors)
    }

    @Test
    fun `createTest should create and return new test`() {
        val request =
            CreateTestRequestDTO(
                snippetId = 1L,
                name = "Test 1",
                inputs = listOf("input1"),
                expectedOutputs = listOf("output1"),
            )
        val savedTest =
            TestEntity(
                id = 1L,
                snippetId = 1L,
                name = "Test 1",
                inputs = listOf("input1"),
                expectedOutputs = listOf("output1"),
            )

        every { testRepository.save(any<TestEntity>()) } returns savedTest

        val response = controller.createTest(request)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1L, response.body!!.id)
        assertEquals("Test 1", response.body!!.name)

        verify { testRepository.save(any<TestEntity>()) }
    }

    @Test
    fun `createTest should create test with empty inputs and outputs`() {
        val request =
            CreateTestRequestDTO(
                snippetId = 1L,
                name = "Test Empty",
                inputs = null,
                expectedOutputs = null,
            )
        val savedTest =
            TestEntity(
                id = 2L,
                snippetId = 1L,
                name = "Test Empty",
                inputs = emptyList(),
                expectedOutputs = emptyList(),
            )

        every { testRepository.save(any<TestEntity>()) } returns savedTest

        val response = controller.createTest(request)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(0, response.body!!.inputs.size)
        assertEquals(0, response.body!!.expectedOutputs.size)
    }

    @Test
    fun `updateTest should update and return test`() {
        val testId = 1L
        val request =
            UpdateTestRequestDTO(
                name = "Updated Test",
                inputs = listOf("new-input"),
                expectedOutputs = listOf("new-output"),
            )
        val existingTest =
            TestEntity(
                id = testId,
                snippetId = 1L,
                name = "Old Test",
                inputs = listOf("old-input"),
                expectedOutputs = listOf("old-output"),
            )
        val updatedTest =
            existingTest.copy(
                name = "Updated Test",
                inputs = listOf("new-input"),
                expectedOutputs = listOf("new-output"),
            )

        every { testRepository.findById(testId) } returns Optional.of(existingTest)
        every { testRepository.save(any<TestEntity>()) } returns updatedTest

        val response = controller.updateTest(testId, request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals("Updated Test", response.body!!.name)
        assertEquals(listOf("new-input"), response.body!!.inputs)

        verify { testRepository.findById(testId) }
        verify { testRepository.save(any<TestEntity>()) }
    }

    @Test
    fun `updateTest should update only specified fields`() {
        val testId = 1L
        val request =
            UpdateTestRequestDTO(
                name = "Updated Name Only",
                inputs = null,
                expectedOutputs = null,
            )
        val existingTest =
            TestEntity(
                id = testId,
                snippetId = 1L,
                name = "Old Name",
                inputs = listOf("input"),
                expectedOutputs = listOf("output"),
            )
        val updatedTest = existingTest.copy(name = "Updated Name Only")

        every { testRepository.findById(testId) } returns Optional.of(existingTest)
        every { testRepository.save(any<TestEntity>()) } returns updatedTest

        val response = controller.updateTest(testId, request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Updated Name Only", response.body!!.name)
        assertEquals(listOf("input"), response.body!!.inputs)
    }

    @Test
    fun `updateTest should throw exception when test not found`() {
        val testId = 999L
        val request = UpdateTestRequestDTO(name = "Test", inputs = null, expectedOutputs = null)

        every { testRepository.findById(testId) } returns Optional.empty()

        assertThrows(NoSuchElementException::class.java) {
            controller.updateTest(testId, request)
        }

        verify { testRepository.findById(testId) }
    }

    @Test
    fun `getTestsBySnippet should return all tests for snippet`() {
        val snippetId = 1L
        val tests =
            listOf(
                TestEntity(1L, snippetId, "Test 1", listOf("in1"), listOf("out1")),
                TestEntity(2L, snippetId, "Test 2", listOf("in2"), listOf("out2")),
            )

        every { testRepository.findBySnippetId(snippetId) } returns tests

        val response = controller.getTestsBySnippet(snippetId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body!!.size)
        assertEquals("Test 1", response.body!![0].name)

        verify { testRepository.findBySnippetId(snippetId) }
    }

    @Test
    fun `getTestsBySnippet should return empty list when no tests found`() {
        val snippetId = 999L

        every { testRepository.findBySnippetId(snippetId) } returns emptyList()

        val response = controller.getTestsBySnippet(snippetId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(0, response.body!!.size)
    }

    @Test
    fun `getTest should return test by id`() {
        val testId = 1L
        val test = TestEntity(testId, 1L, "Test 1", listOf("in"), listOf("out"))

        every { testRepository.findById(testId) } returns Optional.of(test)

        val response = controller.getTest(testId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(testId, response.body!!.id)
        assertEquals("Test 1", response.body!!.name)

        verify { testRepository.findById(testId) }
    }

    @Test
    fun `getTest should throw exception when test not found`() {
        val testId = 999L

        every { testRepository.findById(testId) } returns Optional.empty()

        assertThrows(NoSuchElementException::class.java) {
            controller.getTest(testId)
        }

        verify { testRepository.findById(testId) }
    }

    @Test
    fun `deleteTest should delete test by id`() {
        val testId = 1L

        every { testRepository.deleteById(testId) } returns Unit

        val response = controller.deleteTest(testId)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify { testRepository.deleteById(testId) }
    }
}
