package dtos

data class FormatterRuleDTO(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val value: Any? = null,
)
