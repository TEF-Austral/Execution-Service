package services

import dtos.ValidationResultDTO
import java.io.InputStream

interface LanguageAnalyzerService {
    fun compile(
        src: InputStream,
        version: String,
    ): ValidationResultDTO

    fun analyze(
        src: InputStream,
        version: String,
        userId: String,
    ): ValidationResultDTO

    fun supportsLanguage(): String
}
