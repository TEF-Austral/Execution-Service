package entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "formatter_configs")
data class FormatterConfigEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, unique = true)
    val userId: String,
    @Column(nullable = false)
    val spaceBeforeColon: Boolean = false,
    @Column(nullable = false)
    val spaceAfterColon: Boolean = true,
    @Column(nullable = false)
    val spaceAroundAssignment: Boolean = true,
    @Column(nullable = false)
    val blankLinesAfterPrintln: Int = 1,
    @Column(nullable = false)
    val indentSize: Int = 4,
    @Column(nullable = false)
    val ifBraceOnSameLine: Boolean = true,
    @Column(nullable = false)
    val enforceSingleSpace: Boolean = true,
    @Column(nullable = false)
    val spaceAroundOperators: Boolean = true,
)
