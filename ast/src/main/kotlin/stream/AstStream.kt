package stream

import node.ASTNode

interface AstStream {

    fun peak(): ASTNode?

    fun next(): AstStreamResult

    fun isAtEnd(): Boolean
}
