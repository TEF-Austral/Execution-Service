package mappers

import dtos.FormatConfigDTO
import dtos.FormatterRuleDTO
import entities.FormatterConfigEntity
import formatter.config.FormatConfig

interface FormatterConfigMapper {
    fun entityToRules(entity: FormatterConfigEntity): List<FormatterRuleDTO>

    fun rulesToConfigDTO(rules: List<FormatterRuleDTO>): FormatConfigDTO

    fun dtoToFormatConfig(configDTO: FormatConfigDTO): FormatConfig

    fun rulesToEntity(
        userId: String,
        rules: List<FormatterRuleDTO>,
    ): FormatterConfigEntity

    fun updateEntity(
        entity: FormatterConfigEntity,
        rules: List<FormatterRuleDTO>,
    ): FormatterConfigEntity
}
