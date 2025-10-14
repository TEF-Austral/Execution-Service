package node

import coordinates.Coordinates

interface ASTNode {
    fun getCoordinates(): Coordinates
}
