package api.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "snippet")
data class Snippet(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Long = 0,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val snippetInBucket: Long,
    @Column(nullable = false, name = "owner_id")
    val ownerId: String,
    @Column
    val deletedAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val language: Language,
    @Column(nullable = false)
    val version: String,
    @OneToMany(mappedBy = "snippet", cascade = [CascadeType.ALL], orphanRemoval = true)
    val tests: MutableList<Test> = mutableListOf(),
)
