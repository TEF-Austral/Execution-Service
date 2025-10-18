package statement.enforcers

import parser.result.SemanticResult

sealed interface SemanticEnforcers {
    fun enforce(result: SemanticResult): SemanticResult
}
