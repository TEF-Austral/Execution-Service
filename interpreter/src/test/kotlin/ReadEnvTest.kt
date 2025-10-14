import coordinates.Position
import data.DataBase
import executor.expression.ReadEnvExpressionExecutor
import input.EnvInputProvider
import node.ReadEnvExpression
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import type.CommonTypes
import variable.Variable

class ReadEnvTest {

    class DummyDataBase : DataBase {
        private val variables = mutableMapOf<String, Variable>()
        private val constants = mutableMapOf<String, Variable>()

        override fun getVariables(): Map<String, Variable> = variables

        override fun getConstants(): Map<String, Variable> = constants

        override fun addVariable(
            key: String,
            value: Variable,
        ): DataBase {
            variables[key] = value
            return this
        }

        override fun addConstant(
            key: String,
            value: Variable,
        ): DataBase {
            constants[key] = value
            return this
        }

        override fun changeVariableValue(
            key: String,
            value: Variable,
        ): DataBase {
            if (variables.containsKey(key)) {
                variables[key] = value
            }
            return this
        }

        override fun getVariableValue(key: String): Any? = variables[key]?.getValue()

        override fun getConstantValue(key: String): Any? = constants[key]?.getValue()

        override fun isConstant(key: String): Boolean = constants.containsKey(key)

        override fun getValue(key: String): Any? =
            variables[key]?.getValue() ?: constants[key]?.getValue()
    }

    @Test
    fun `test read existing environment variable`() {
        val envVarName = "PATH"
        val expression = ReadEnvExpression(envVarName, Position(0, 0))
        val executor = ReadEnvExpressionExecutor(EnvInputProvider())

        val result = executor.execute(expression, database = DummyDataBase())

        assertTrue(result.interpretedCorrectly)
        assertNotNull(result.interpreter)
        assertEquals(CommonTypes.STRING_LITERAL, result.interpreter?.getType())
        assertEquals(System.getenv(envVarName), result.interpreter?.getValue())
        assertEquals("Successfully read environment variable '$envVarName'.", result.message)
    }

    @Test
    fun `test read non-existing environment variable`() {
        val envVarName = "NON_EXISTENT_VARIABLE_FOR_TESTING_12345"
        val expression = ReadEnvExpression(envVarName, Position(0, 0))
        val executor = ReadEnvExpressionExecutor(EnvInputProvider())

        val result = executor.execute(expression, DummyDataBase())

        assertFalse(result.interpretedCorrectly)
        assertNull(result.interpreter)
        assertEquals("Error: Environment variable '$envVarName' not found.", result.message)
    }
}
