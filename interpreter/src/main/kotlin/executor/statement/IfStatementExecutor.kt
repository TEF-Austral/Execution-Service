package executor.statement

import data.DataBase
import executor.expression.DefaultExpressionExecutor
import node.IfStatement
import node.Statement
import result.InterpreterResult
import type.CommonTypes // Asegúrate de importar tu enum

class IfStatementExecutor(
    private val expressionExecutor: DefaultExpressionExecutor,
    private val statementExecutor: DefaultStatementExecutor,
) : SpecificStatementExecutor {
    override fun canHandle(statement: Statement): Boolean = statement is IfStatement

    override fun execute(
        statement: Statement,
        database: DataBase,
    ): InterpreterResult {
        return try {
            val ifStmt = statement as IfStatement

            // 1. Evaluar la expresión de la condición
            val conditionResult = expressionExecutor.execute(ifStmt.getCondition(), database)
            if (!conditionResult.interpretedCorrectly) {
                return conditionResult
            }

            // 2. Verificar que el resultado de la condición sea un booleano
            val conditionValue =
                conditionResult.interpreter
                    ?: return createErrorResult("Condition evaluation returned null.")

            if (!isValidType(conditionValue.getType())) {
                return createErrorResult(
                    "If condition must evaluate to a boolean value, but got: ${conditionValue.getType()}.",
                )
            }

            val isTrue = conditionValue.getValue().toString().toBoolean()

            // 3. Ejecutar el bloque de código correspondiente
            if (isTrue) {
                // Si la condición es verdadera, ejecutar el bloque 'consequence'
                statementExecutor.execute(ifStmt.getConsequence(), database)
            } else if (ifStmt.hasAlternative()) {
                statementExecutor.execute(ifStmt.getAlternative()!!, database)
            } else {
                // Si es falsa y no hay 'else', la ejecución es exitosa y no se hace nada
                InterpreterResult(true, "Condición falsa sin bloque 'else'.", null)
            }
        } catch (e: Exception) {
            // Manejador para cualquier error inesperado durante la ejecución
            createErrorResult("Error inesperado ejecutando la sentencia 'if': ${e.message}")
        }
    }

    private fun createErrorResult(message: String): InterpreterResult =
        InterpreterResult(false, message, null)

    private fun isValidType(type: CommonTypes): Boolean =
        when (type) {
            CommonTypes.BOOLEAN_LITERAL -> true
            CommonTypes.BOOLEAN -> true
            else -> false
        }
}
