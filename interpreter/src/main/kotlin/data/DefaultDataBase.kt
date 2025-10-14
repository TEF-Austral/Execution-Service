package data

import variable.Variable

class DefaultDataBase(
    private val variables: Map<String, Variable> = emptyMap(),
    private val constants: Map<String, Variable> = emptyMap(),
) : DataBase {
    override fun getVariables(): Map<String, Variable> = variables

    override fun getConstants(): Map<String, Variable> = constants

    override fun addVariable(
        key: String,
        value: Variable,
    ): DataBase = DefaultDataBase(variables + (key to value), constants)

    override fun addConstant(
        key: String,
        value: Variable,
    ): DataBase {
        if (value.getValue() == null) {
            return this
        }
        return DefaultDataBase(variables, constants + (key to value))
    }

    override fun changeVariableValue(
        key: String,
        value: Variable,
    ): DataBase = DefaultDataBase(variables + (key to value), constants)

    override fun getVariableValue(key: String): Any? = variables[key]

    override fun getConstantValue(key: String): Any? = constants[key]

    override fun isConstant(key: String): Boolean = constants.containsKey(key)

    override fun getValue(key: String): Any? = getVariableValue(key) ?: getConstantValue(key)

    fun clear(): DefaultDataBase = DefaultDataBase()
}
