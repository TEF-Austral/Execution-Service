package services

import dtos.ValidationResultDTO
import helpers.GetAnalyzerConfig
import repositories.AnalyzerRepository
import kotlin.test.assertTrue
import kotlin.test.Test
import java.util.Optional
import entities.AnalyzerEntity
import rules.AstValidator
import rules.rules.NoReadEnvRule
import rules.validation.CompositeValidator

class AnalyzerServiceV11Test {

    private val rule = NoReadEnvRule()

    private val validator: AstValidator = CompositeValidator(rules = listOf(rule))

    private val service = PrintScriptAnalyzerService(fakeGetAnalyzerConfig(), validator)

    private fun validateFromResource(path: String): ValidationResultDTO {
        val stream =
            this::class.java.getResourceAsStream(path)
                ?: throw IllegalArgumentException("Resource not found: $path")
        stream.use {
            return service.analyze(it, "1.1", "1")
        }
    }

    @Test
    fun valid_files_should_be_valid() {
        val files =
            listOf(
                "/printscript/v1_1/valid1.txt",
                "/printscript/v1_1/valid2.txt",
                "/printscript/v1_1/valid3.txt",
                "/printscript/v1_1/valid4.txt",
                "/printscript/v1_1/valid5.txt",
            )

        files.forEach { path ->
            val result = validateFromResource(path)
            assertTrue(
                result is ValidationResultDTO.Valid,
                "Expected Valid for $path but was $result",
            )
        }
    }

    @Test
    fun invalid_files_should_be_invalid() {
        val files =
            listOf(
                "/printscript/v1_1/invalid1.txt",
                "/printscript/v1_1/invalid2.txt",
                "/printscript/v1_1/invalid3.txt",
                "/printscript/v1_1/invalid4.txt",
                "/printscript/v1_1/invalid5.txt",
            )

        files.forEach { path ->
            val result = validateFromResource(path)
            assertTrue(
                result is ValidationResultDTO.Invalid,
                "Expected Invalid for $path but was $result",
            )
        }
    }

    private fun fakeGetAnalyzerConfig(): GetAnalyzerConfig {
        val proxy =
            java.lang.reflect.Proxy.newProxyInstance(
                AnalyzerRepository::class.java.classLoader,
                arrayOf(AnalyzerRepository::class.java),
            ) { _, method, _ ->
                when (method.name) {
                    "findById" -> Optional.empty<AnalyzerEntity>()
                    else -> throw UnsupportedOperationException("Not used in tests: ${method.name}")
                }
            } as AnalyzerRepository
        return GetAnalyzerConfig(proxy)
    }
}
