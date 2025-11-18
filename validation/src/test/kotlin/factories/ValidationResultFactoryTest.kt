package factories

import diagnostic.Diagnostic
import dtos.ValidationResultDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import parser.result.FinalResult
import position.Position

class ValidationResultFactoryTest {

    private lateinit var factory: ValidationResultFactory

    @BeforeEach
    fun setup() {
        factory = ValidationResultFactory()
    }

    @Test
    fun testCreateFromParserResultSuccess() {
        val result = mock(FinalResult::class.java)
        `when`(result.isSuccess()).thenReturn(true)

        val validationResult = factory.createFromParserResult(result)

        assertTrue(validationResult is ValidationResultDTO.Valid)
    }

    @Test
    fun testCreateFromParserResultFailure() {
        val result = mock(FinalResult::class.java)
        val position = mock(Position::class.java)
        `when`(result.isSuccess()).thenReturn(false)
        `when`(result.getCoordinates()).thenReturn(position)
        `when`(result.message()).thenReturn("Parse error")
        `when`(position.getRow()).thenReturn(1)
        `when`(position.getColumn()).thenReturn(5)

        val validationResult = factory.createFromParserResult(result)

        assertTrue(validationResult is ValidationResultDTO.Invalid)
        val invalid = validationResult as ValidationResultDTO.Invalid
        assertEquals(1, invalid.violations.size)
        assertEquals("Parse error", invalid.violations[0].message)
        assertEquals(1, invalid.violations[0].line)
        assertEquals(5, invalid.violations[0].column)
    }

    @Test
    fun testCreateFromParserResultFailureWithoutPosition() {
        val result = mock(FinalResult::class.java)
        `when`(result.isSuccess()).thenReturn(false)
        `when`(result.getCoordinates()).thenReturn(null)
        `when`(result.message()).thenReturn("Unknown error")

        val validationResult = factory.createFromParserResult(result)

        assertTrue(validationResult is ValidationResultDTO.Invalid)
        val invalid = validationResult as ValidationResultDTO.Invalid
        assertEquals(1, invalid.violations.size)
        assertEquals("Unknown error", invalid.violations[0].message)
        assertEquals(-1, invalid.violations[0].line)
        assertEquals(-1, invalid.violations[0].column)
    }

    @Test
    fun testCreateFromDiagnosticsEmpty() {
        val validationResult = factory.createFromDiagnostics(emptyList())

        assertTrue(validationResult is ValidationResultDTO.Valid)
    }

    @Test
    fun testCreateFromDiagnosticsWithViolations() {
        val position1 = mock(Position::class.java)
        `when`(position1.getRow()).thenReturn(1)
        `when`(position1.getColumn()).thenReturn(0)

        val position2 = mock(Position::class.java)
        `when`(position2.getRow()).thenReturn(2)
        `when`(position2.getColumn()).thenReturn(5)

        val diagnostic1 = Diagnostic("Error 1", position1)
        val diagnostic2 = Diagnostic("Error 2", position2)

        val validationResult = factory.createFromDiagnostics(listOf(diagnostic1, diagnostic2))

        assertTrue(validationResult is ValidationResultDTO.Invalid)
        val invalid = validationResult as ValidationResultDTO.Invalid
        assertEquals(2, invalid.violations.size)
        assertEquals("Error 1", invalid.violations[0].message)
        assertEquals(1, invalid.violations[0].line)
        assertEquals("Error 2", invalid.violations[1].message)
        assertEquals(2, invalid.violations[1].line)
    }
}
