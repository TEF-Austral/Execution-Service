package entities

import checkers.IdentifierStyle
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType

@Table(name = "analyzer_rules")
@Entity
data class AnalyzerEntity(
    @Id
    val userId: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "identifier_style")
    val identifierStyle: IdentifierStyle = IdentifierStyle.NO_STYLE,

    @Column(name = "restrict_println_args")
    val restrictPrintlnArgs: Boolean = true,

    @Column(name = "restrict_read_input_args")
    val restrictReadInputArgs: Boolean = false,

    @Column(name = "no_read_input")
    val noReadInput: Boolean = false
)