package services

import checkers.IdentifierStyle
import dtos.ValidationResultDTO
import entities.AnalyzerEntity
import helpers.GetAnalyzerConfig
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import repositories.AnalyzerRepository
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import java.util.Optional

class AnalyzerServiceWithConfigTest {

    private lateinit var analyzerRepository: AnalyzerRepository
    private lateinit var getAnalyzerConfig: GetAnalyzerConfig
    private lateinit var analyzerService: PrintScriptAnalyzerService

    @BeforeEach
    fun setup() {
        analyzerRepository = mock()
        getAnalyzerConfig = GetAnalyzerConfig(analyzerRepository)
        analyzerService = PrintScriptAnalyzerService(getAnalyzerConfig)
    }

    @Test
    fun `analyze should use default config when user not found`() {
        val userId = "unknownUser"
        val code = "let x: number = 5;\nprintln(x);"

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.empty())

        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val result = analyzerService.analyze(inputStream, "1.1", userId)

        assertTrue(result is ValidationResultDTO.Valid)
    }

    @Test
    fun `analyze should use user specific config for identifierStyle CAMEL_CASE`() {
        val userId = "camelUser"
        val entity =
            AnalyzerEntity(
                userId = userId,
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = false,
                noReadInput = false,
            )

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(entity))

        val validCode = "let myVariable: number = 5;\nprintln(myVariable);"
        val inputStream1 = ByteArrayInputStream(validCode.toByteArray(StandardCharsets.UTF_8))
        val result1 = analyzerService.analyze(inputStream1, "1.1", userId)
        assertTrue(result1 is ValidationResultDTO.Valid)

        val invalidCode = "let my_variable: number = 5;\nprintln(my_variable);"
        val inputStream2 = ByteArrayInputStream(invalidCode.toByteArray(StandardCharsets.UTF_8))
        val result2 = analyzerService.analyze(inputStream2, "1.1", userId)
        assertTrue(result2 is ValidationResultDTO.Invalid)
    }

    @Test
    fun `analyze should use user specific config for identifierStyle SNAKE_CASE`() {
        val userId = "snakeUser"
        val entity =
            AnalyzerEntity(
                userId = userId,
                identifierStyle = IdentifierStyle.SNAKE_CASE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = false,
                noReadInput = false,
            )

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(entity))

        val validCode = "let my_variable: number = 5;\nprintln(my_variable);"
        val inputStream1 = ByteArrayInputStream(validCode.toByteArray(StandardCharsets.UTF_8))
        val result1 = analyzerService.analyze(inputStream1, "1.1", userId)
        assertTrue(result1 is ValidationResultDTO.Valid)

        val invalidCode = "let myVariable: number = 5;\nprintln(myVariable);"
        val inputStream2 = ByteArrayInputStream(invalidCode.toByteArray(StandardCharsets.UTF_8))
        val result2 = analyzerService.analyze(inputStream2, "1.1", userId)
        assertTrue(result2 is ValidationResultDTO.Invalid)
    }

    @Test
    fun `analyze should use user specific config for identifierStyle NO_STYLE`() {
        val userId = "noStyleUser"
        val entity =
            AnalyzerEntity(
                userId = userId,
                identifierStyle = IdentifierStyle.NO_STYLE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = false,
                noReadInput = false,
            )

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(entity))

        val camelCode = "let myVariable: number = 5;\nprintln(myVariable);"
        val inputStream1 = ByteArrayInputStream(camelCode.toByteArray(StandardCharsets.UTF_8))
        val result1 = analyzerService.analyze(inputStream1, "1.1", userId)
        assertTrue(result1 is ValidationResultDTO.Valid)

        val snakeCode = "let my_variable: number = 5;\nprintln(my_variable);"
        val inputStream2 = ByteArrayInputStream(snakeCode.toByteArray(StandardCharsets.UTF_8))
        val result2 = analyzerService.analyze(inputStream2, "1.1", userId)
        assertTrue(result2 is ValidationResultDTO.Valid)
    }

    @Test
    fun `analyze should use restrictPrintlnArgs config`() {
        val userRestricted = "restrictedUser"
        val entityRestricted =
            AnalyzerEntity(
                userId = userRestricted,
                identifierStyle = IdentifierStyle.NO_STYLE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = false,
                noReadInput = false,
            )

        val userUnrestricted = "unrestrictedUser"
        val entityUnrestricted =
            AnalyzerEntity(
                userId = userUnrestricted,
                identifierStyle = IdentifierStyle.NO_STYLE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = false,
                noReadInput = false,
            )

        whenever(
            analyzerRepository.findById(userRestricted),
        ).thenReturn(Optional.of(entityRestricted))
        whenever(
            analyzerRepository.findById(userUnrestricted),
        ).thenReturn(Optional.of(entityUnrestricted))

        val code = "println(1 + 2);"

        val inputStream1 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val result1 = analyzerService.analyze(inputStream1, "1.1", userRestricted)
        assertTrue(result1 is ValidationResultDTO.Invalid)

        val inputStream2 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val result2 = analyzerService.analyze(inputStream2, "1.1", userUnrestricted)
        assertTrue(result2 is ValidationResultDTO.Valid)
    }

    @Test
    fun `analyze should use restrictReadInputArgs config`() {
        val userRestricted = "restrictedReadUser"
        val entityRestricted =
            AnalyzerEntity(
                userId = userRestricted,
                identifierStyle = IdentifierStyle.NO_STYLE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = true,
                noReadInput = false,
            )

        val userUnrestricted = "unrestrictedReadUser"
        val entityUnrestricted =
            AnalyzerEntity(
                userId = userUnrestricted,
                identifierStyle = IdentifierStyle.NO_STYLE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = false,
                noReadInput = false,
            )

        whenever(
            analyzerRepository.findById(userRestricted),
        ).thenReturn(Optional.of(entityRestricted))
        whenever(
            analyzerRepository.findById(userUnrestricted),
        ).thenReturn(Optional.of(entityUnrestricted))

        val code = "let x: string = readInput(\"hello\" + \"world\");\nprintln(x);"

        val inputStream1 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val result1 = analyzerService.analyze(inputStream1, "1.1", userRestricted)
        assertTrue(result1 is ValidationResultDTO.Invalid)

        val inputStream2 = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val result2 = analyzerService.analyze(inputStream2, "1.1", userUnrestricted)
        assertTrue(result2 is ValidationResultDTO.Valid)
    }

    @Test
    fun `analyze should handle different users with different configs independently`() {
        val user1 = "user1"
        val entity1 =
            AnalyzerEntity(
                userId = user1,
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = false,
                noReadInput = false,
            )

        val user2 = "user2"
        val entity2 =
            AnalyzerEntity(
                userId = user2,
                identifierStyle = IdentifierStyle.SNAKE_CASE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = true,
                noReadInput = false,
            )

        whenever(analyzerRepository.findById(user1)).thenReturn(Optional.of(entity1))
        whenever(analyzerRepository.findById(user2)).thenReturn(Optional.of(entity2))

        val camelCode = "let myVar: number = 5;\nprintln(myVar);"
        val inputStream1 = ByteArrayInputStream(camelCode.toByteArray(StandardCharsets.UTF_8))
        val result1 = analyzerService.analyze(inputStream1, "1.1", user1)
        assertTrue(result1 is ValidationResultDTO.Valid)

        val snakeCode = "let my_var: number = 5;\nprintln(my_var);"
        val inputStream2 = ByteArrayInputStream(snakeCode.toByteArray(StandardCharsets.UTF_8))
        val result2 = analyzerService.analyze(inputStream2, "1.1", user2)
        assertTrue(result2 is ValidationResultDTO.Valid)
    }

    @Test
    fun `analyze should work with version 1_0 and user config`() {
        val userId = "v10User"
        val entity =
            AnalyzerEntity(
                userId = userId,
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = false,
                noReadInput = false,
            )

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(entity))

        val code = "let myVar: number = 5;\nprintln(myVar);"
        val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
        val result = analyzerService.analyze(inputStream, "1.0", userId)

        assertTrue(result is ValidationResultDTO.Valid)
    }

    @Test
    fun `analyze should detect violations with custom config`() {
        val userId = "violationUser"
        val entity =
            AnalyzerEntity(
                userId = userId,
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = false,
                noReadInput = false,
            )

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(entity))

        val invalidCode =
            """
            let invalid_name: number = 5;
            println(1 + 2);
            """.trimIndent()

        val inputStream = ByteArrayInputStream(invalidCode.toByteArray(StandardCharsets.UTF_8))
        val result = analyzerService.analyze(inputStream, "1.1", userId)

        assertTrue(result is ValidationResultDTO.Invalid)
        val invalid = result as ValidationResultDTO.Invalid
        assertTrue(invalid.violations.isNotEmpty())
    }

    @Test
    fun `compile should work independently of user config`() {
        val userId = "compileUser"
        val entity =
            AnalyzerEntity(
                userId = userId,
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = true,
                noReadInput = true,
            )

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(entity))

        val validSyntax = "let x: number = 5;\nprintln(x);"
        val inputStream1 = ByteArrayInputStream(validSyntax.toByteArray(StandardCharsets.UTF_8))
        val result1 = analyzerService.compile(inputStream1, "1.1")
        assertTrue(result1 is ValidationResultDTO.Valid)

        val invalidSyntax = "let x: number = 5"
        val inputStream2 = ByteArrayInputStream(invalidSyntax.toByteArray(StandardCharsets.UTF_8))
        val result2 = analyzerService.compile(inputStream2, "1.1")
        assertTrue(result2 is ValidationResultDTO.Invalid)
    }

    @Test
    fun `analyze should handle all config combinations correctly`() {
        val testCases =
            listOf(
                Triple(IdentifierStyle.CAMEL_CASE, true, "let myVar: number = 5;\nprintln(myVar);"),
                Triple(
                    IdentifierStyle.SNAKE_CASE,
                    true,
                    "let my_var: number = 5;\nprintln(my_var);",
                ),
                Triple(
                    IdentifierStyle.NO_STYLE,
                    true,
                    "let anyStyle: number = 5;\nprintln(anyStyle);",
                ),
                Triple(
                    IdentifierStyle.CAMEL_CASE,
                    false,
                    "let my_var: number = 5;\nprintln(my_var);",
                ),
                Triple(
                    IdentifierStyle.SNAKE_CASE,
                    false,
                    "let myVar: number = 5;\nprintln(myVar);",
                ),
            )

        testCases.forEachIndexed { index, (style, shouldBeValid, code) ->
            val userId = "comboUser$index"
            val entity =
                AnalyzerEntity(
                    userId = userId,
                    identifierStyle = style,
                    restrictPrintlnArgs = false,
                    restrictReadInputArgs = false,
                    noReadInput = false,
                )

            whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(entity))

            val inputStream = ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
            val result = analyzerService.analyze(inputStream, "1.1", userId)

            if (shouldBeValid) {
                assertTrue(result is ValidationResultDTO.Valid, "Expected valid for case $index")
            } else {
                assertTrue(
                    result is ValidationResultDTO.Invalid,
                    "Expected invalid for case $index",
                )
            }
        }
    }
}
