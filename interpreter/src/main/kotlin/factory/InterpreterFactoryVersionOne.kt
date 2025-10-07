package factory

import DefaultInterpreter
import data.DefaultDataBase
import emitter.Emitter
import emitter.PrintEmitter
import executor.coercer.StringToNumberConverter
import executor.coercer.StringToStringConverter
import executor.coercer.TypeCoercer
import executor.expression.BinaryExpressionExecutor
import executor.expression.DefaultExpressionExecutor
import executor.expression.IdentifierExpressionExecutor
import executor.expression.LiteralExpressionExecutor
import executor.expression.SpecificExpressionExecutor
import executor.operators.Divide
import executor.operators.Multiplication
import executor.operators.Operator
import executor.operators.Subtraction
import executor.operators.Sum
import executor.statement.AssignmentStatementExecutor
import executor.statement.DeclarationStatementExecutor
import executor.statement.DefaultStatementExecutor
import executor.statement.ExpressionStatementExecutor
import executor.statement.LetDeclarationStatement
import executor.statement.PrintStatementExecutor
import executor.statement.SpecificStatementExecutor

object InterpreterFactoryVersionOne {

    private val dataBase: DefaultDataBase = DefaultDataBase()

    val operators: List<Operator> =
        listOf(Sum, Divide, Multiplication, Subtraction)

    private val identifierAndLiteralExecutors: List<SpecificExpressionExecutor> =
        listOf(
            IdentifierExpressionExecutor(),
            LiteralExpressionExecutor(),
        )

    private fun getExpressionExecutors(): List<SpecificExpressionExecutor> =
        listOf(
            BinaryExpressionExecutor(expressions = identifierAndLiteralExecutors),
            IdentifierExpressionExecutor(),
            LiteralExpressionExecutor(),
        )

    private val coercers: TypeCoercer =
        TypeCoercer(
            listOf(
                StringToNumberConverter(),
                StringToStringConverter(),
            ),
        )

    fun getStatements(
        dataBase: DefaultDataBase,
        coercers: TypeCoercer,
        allSpecificExpressionExecutors: List<SpecificExpressionExecutor>,
        emitter: Emitter,
    ): DefaultStatementExecutor {
        val expressionExecutor = DefaultExpressionExecutor(allSpecificExpressionExecutors)

        val statementSpecialists = mutableListOf<SpecificStatementExecutor>()
        val mainStatementExecutor = DefaultStatementExecutor(statementSpecialists)

        val letDeclaration = LetDeclarationStatement(expressionExecutor, coercers)

        val declarationExecutor =
            DeclarationStatementExecutor(
                expressionExecutor,
                listOf(letDeclaration),
            )

        val assignmentExecutor = AssignmentStatementExecutor(expressionExecutor)
        val printExecutor = PrintStatementExecutor(expressionExecutor, emitter)
        val expressionStatementExecutor = ExpressionStatementExecutor(expressionExecutor)

        statementSpecialists.add(declarationExecutor)
        statementSpecialists.add(assignmentExecutor)
        statementSpecialists.add(printExecutor)
        statementSpecialists.add(expressionStatementExecutor)

        return mainStatementExecutor
    }

    fun createDefaultInterpreter(emitter: Emitter = PrintEmitter()): DefaultInterpreter {
        dataBase.clear()
        val expressionExecutors = getExpressionExecutors()
        return DefaultInterpreter(
            dataBase,
            DefaultExpressionExecutor(expressionExecutors),
            getStatements(dataBase, coercers, expressionExecutors, emitter),
        )
    }
}
