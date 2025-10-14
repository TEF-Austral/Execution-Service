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

class LetDeclarationStatement(
    private val defaultExpressionExecutor: DefaultExpressionExecutor,
    private val typeCoercer: ITypeCoercer,
) : SpecificStatementExecutor {
    override fun canHandle(statement: Statement): Boolean =
        statement is DeclarationStatement && statement.getDeclarationType() == CommonTypes.LET

    override fun execute(
        statement: Statement,
        database: DataBase,
    ): InterpreterResult {
        val declarationStatement = statement as DeclarationStatement
        val identifier = declarationStatement.getIdentifier()
        val declaredType = declarationStatement.getDataType()
        val initialValueExpression = declarationStatement.getInitialValue()

        val finalVariable: Variable =
            if (initialValueExpression != null) {
                val expressionResult =
                    defaultExpressionExecutor.execute(
                        initialValueExpression,
                        database,
                    )
                if (!expressionResult.interpretedCorrectly) return expressionResult

                val initialValue =
                    expressionResult.interpreter
                        ?: return InterpreterResult(false, "Expression yielded no value", null)

                if (initialValueExpression is CoercibleExpression) {
                    val coercionResult = typeCoercer.coerce(expressionResult, declaredType)
                    if (!coercionResult.interpretedCorrectly) return coercionResult
                    coercionResult.interpreter!!
                } else {
                    if (!areTypesCompatible(declaredType, initialValue.getType())) {
                        return InterpreterResult(
                            false,
                            "Type mismatch: Expected '$declaredType', got '${initialValue.getType()}'",
                            null,
                        )
                    }
                    Variable(declaredType, initialValue.getValue())
                }
            } else {
                Variable(declaredType, null)
            }

        val newDatabase = database.addVariable(identifier, finalVariable)
        return InterpreterResult(
            true,
            "Declaration successful",
            finalVariable,
            newDatabase,
        )
    }
}
