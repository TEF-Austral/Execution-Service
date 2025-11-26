package services

import dtos.FormatConfigDTO
import java.io.InputStream

interface LanguageFormatterService {
    fun format(
        src: InputStream,
        version: String,
        configDTO: FormatConfigDTO,
    ): String

    fun supportsLanguage(): String
}
