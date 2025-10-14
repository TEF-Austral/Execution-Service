package result

import Token
import TokenStream

data class StreamResult(
    val token: Token,
    val nextStream: TokenStream,
)
