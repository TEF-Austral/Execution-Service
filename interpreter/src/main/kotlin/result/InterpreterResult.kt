package result

import data.DataBase
import variable.Variable

data class InterpreterResult(
    val interpretedCorrectly: Boolean,
    val message: String,
    val interpreter: Variable?,
    val updatedDatabase: DataBase? = null,
)
