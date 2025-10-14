package result

import Lexer
import Token

data class LexerResult(
    val token: Token,
    val lexer: Lexer,
)
