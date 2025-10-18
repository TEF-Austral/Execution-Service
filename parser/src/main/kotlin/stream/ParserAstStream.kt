package parser.stream

import node.ASTNode
import stream.AstStream
import stream.AstStreamResult
import node.EmptyExpression
import parser.ParserInterface

class ParserAstStream(
    private val parser: ParserInterface,
) : AstStream {

    override fun peak(): ASTNode {
        if (isAtEnd()) {
            return EmptyExpression()
        }

        val result = parser.next()

        return if (result.isSuccess) {
            result.node
        } else {
            EmptyExpression()
        }
    }

    override fun next(): AstStreamResult {
        if (isAtEnd()) {
            return failedAstStreamResult(
                parser,
                "Cannot call 'next' because the stream is at the end.",
            )
        }

        val result = parser.next()

        return if (result.isSuccess) {
            val nextNode = result.node
            val nextParser = result.parser
            AstStreamResult(nextNode, ParserAstStream(nextParser), true)
        } else {
            failedAstStreamResult(parser, result.message)
        }
    }

    override fun isAtEnd(): Boolean = parser.isAtEnd()
}

private fun failedAstStreamResult(
    parser: ParserInterface,
    message: String? = null,
): AstStreamResult = AstStreamResult(EmptyExpression(), ParserAstStream(parser), false, message)
