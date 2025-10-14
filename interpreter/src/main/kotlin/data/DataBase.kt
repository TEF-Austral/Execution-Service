package data

import variable.Variable

interface DataBase {
    fun getVariables(): Map<String, Variable>

    fun getConstants(): Map<String, Variable>

    fun addVariable(
        key: String,
        value: Variable,
    ): DataBase

    fun addConstant(
        key: String,
        value: Variable,
    ): DataBase

    fun changeVariableValue(
        key: String,
        value: Variable,
    ): DataBase

    fun getVariableValue(key: String): Any?

    fun getConstantValue(key: String): Any?

    fun isConstant(key: String): Boolean

    fun getValue(key: String): Any?
}
