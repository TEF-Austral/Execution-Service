package services

import Language
import dtos.FormatConfigDTO
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class LanguagesFormatterService(
    private val formatterServices: List<LanguageFormatterService>,
) {
    private val log = org.slf4j.LoggerFactory.getLogger(LanguagesFormatterService::class.java)

    fun format(
        src: InputStream,
        version: String,
        configDTO: FormatConfigDTO,
        language: Language,
    ): String {
        log.info("Formatting code for language $language, version $version")
        val service =
            formatterServices.find { it.supportsLanguage() == language.name }
                ?: throw IllegalArgumentException("Unsupported language: $language")
        return service.format(src, version, configDTO)
    }
}
