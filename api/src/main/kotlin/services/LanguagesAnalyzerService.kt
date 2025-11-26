package services

import Language
import dtos.ValidationResultDTO
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class LanguagesAnalyzerService(
    private val analyzerServices: List<LanguageAnalyzerService>,
) {
    private val log = org.slf4j.LoggerFactory.getLogger(LanguagesAnalyzerService::class.java)

    fun compile(
        src: InputStream,
        version: String,
        language: Language,
    ): ValidationResultDTO {
        log.info("Compiling code for language $language, version $version")
        val service =
            analyzerServices.find { it.supportsLanguage() == language.name }
                ?: throw IllegalArgumentException("Unsupported language: $language")
        return service.compile(src, version)
    }

    fun analyze(
        src: InputStream,
        version: String,
        userId: String,
        language: Language,
    ): ValidationResultDTO {
        log.info("Analyzing code for language $language, user $userId, version $version")
        val service =
            analyzerServices.find { it.supportsLanguage() == language.name }
                ?: throw IllegalArgumentException("Unsupported language: $language")
        return service.analyze(src, version, userId)
    }
}
