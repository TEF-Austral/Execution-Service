package entities

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OrderColumn
import jakarta.persistence.Table

@Entity
@Table(name = "tests")
data class TestEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val snippetId: Long,
    @Column(nullable = false)
    val name: String,
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "test_inputs", joinColumns = [JoinColumn(name = "test_id")])
    @Column(name = "input_value")
    @OrderColumn(name = "input_order")
    val inputs: List<String> = emptyList(),
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "test_expected_outputs", joinColumns = [JoinColumn(name = "test_id")])
    @Column(name = "output_value")
    @OrderColumn(name = "output_order")
    val expectedOutputs: List<String> = emptyList(),
)
