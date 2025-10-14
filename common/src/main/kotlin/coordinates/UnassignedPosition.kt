package coordinates

class UnassignedPosition : Coordinates {
    override fun getRow(): Int = -1

    override fun getColumn(): Int = -1
}
