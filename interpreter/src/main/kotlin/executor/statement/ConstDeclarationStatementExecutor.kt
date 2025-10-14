package executor.statement

import data.DataBase
import executor.coercer.ITypeCoercer
import executor.expression.DefaultExpressionExecutor
import node.CoercibleExpression
import node.DeclarationStatement
import node.Statement
import result.InterpreterResult
import type.CommonTypes
import utils.areTypesCompatible
import variable.Variable

class ConstDeclarationStatementExecutor(
    private val dataBase: DataBase,
    private val defaultExpressionExecutor: DefaultExpressionExecutor,
    private val typeCoercer: ITypeCoercer, // <-- Inyección de Dependencias
) : SpecificStatementExecutor {
    override fun canHandle(statement: Statement): Boolean =
        statement is DeclarationStatement && statement.getDeclarationType() == CommonTypes.CONST

    override fun execute(
        statement: Statement,
        database: DataBase,
    ): InterpreterResult {
        val declarationStatement = statement as DeclarationStatement
        val identifier = declarationStatement.getIdentifier()

        // 1. Verificar si el identificador ya está en uso
        if (dataBase.isConstant(identifier) || dataBase.getVariables().containsKey(identifier)) {
            return InterpreterResult(false, "Error: '$identifier' has already been declared.", null)
        }

        val initialValueExpression =
            declarationStatement.getInitialValue()
                ?: return InterpreterResult(
                    false,
                    "Error: Constant '$identifier' must be initialized with a value.",
                    null,
                )

        // 2. Ejecutar la expresión para obtener el valor
        val expressionResult = defaultExpressionExecutor.execute(initialValueExpression, database)
        if (!expressionResult.interpretedCorrectly) {
            return expressionResult
        }

        val valueFromExpr =
            expressionResult.interpreter
                ?: return InterpreterResult(
                    false,
                    "Error: Expression for constant '$identifier' did not yield a value.",
                    null,
                )

        if (valueFromExpr.getValue() == null) {
            return InterpreterResult(
                false,
                "Error: Constant '$identifier' cannot be initialized to null.",
                null,
            )
        }

        val declaredType = declarationStatement.getDataType()
        val finalConstant: Variable

        if (initialValueExpression is CoercibleExpression) {
            val interpreter = InterpreterResult(true, "Expression evaluated", valueFromExpr)
            val coercionResult = typeCoercer.coerce(interpreter, declaredType)
            if (!coercionResult.interpretedCorrectly) {
                return coercionResult
            }
            finalConstant = coercionResult.interpreter!!
        } else {
            // Lógica estándar para el resto de expresiones
            if (!areTypesCompatible(declaredType, valueFromExpr.getType())) {
                return InterpreterResult(
                    false,
                    "Error: Type mismatch for constant '$identifier'. Expected '$declaredType' but got '${valueFromExpr.getType()}'.",
                    null,
                )
            }
            finalConstant = Variable(declaredType, valueFromExpr.getValue())
        }

        // 4. Añadir la constante a la base de datos (crear nueva instancia inmutable)
        val newDatabase = dataBase.addConstant(identifier, finalConstant)
        return InterpreterResult(
            true,
            "Constant '$identifier' was successfully declared.",
            finalConstant,
            newDatabase,
        )
    }
}
