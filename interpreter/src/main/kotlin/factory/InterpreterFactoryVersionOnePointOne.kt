package factory

import DefaultInterpreter
import data.DefaultDataBase
import emitter.Emitter
import emitter.PrintEmitter
import executor.coercer.StringToBooleanConverter
import executor.coercer.StringToNumberConverter
import executor.coercer.StringToStringConverter
import executor.coercer.TypeCoercer
import executor.expression.BinaryExpressionExecutor
import executor.expression.DefaultExpressionExecutor
import executor.expression.IdentifierExpressionExecutor
import executor.expression.LiteralExpressionExecutor
import executor.expression.ReadEnvExpressionExecutor
import executor.expression.ReadInputExpressionExecutor
import executor.expression.SpecificExpressionExecutor
import executor.operators.Divide
import executor.operators.LogicalAnd
import executor.operators.LogicalOr
import executor.operators.Multiplication
import executor.operators.Operator
import executor.operators.Subtraction
import executor.operators.Sum
import executor.statement.AssignmentStatementExecutor
import executor.statement.ConstDeclarationStatementExecutor
import executor.statement.DeclarationStatementExecutor
import executor.statement.DefaultStatementExecutor
import executor.statement.ExpressionStatementExecutor
import executor.statement.IfStatementExecutor
import executor.statement.LetDeclarationStatement
import executor.statement.PrintStatementExecutor
import executor.statement.SpecificStatementExecutor
import input.EnvInputProvider
import input.InputProvider
import input.TerminalInputProvider

object InterpreterFactoryVersionOnePointOne {

    private val dataBase: DefaultDataBase = DefaultDataBase()

    val operators: List<Operator> =
        listOf(Sum, Divide, Multiplication, Subtraction, LogicalOr, LogicalAnd)

    private val identifierAndLiteralExecutors: List<SpecificExpressionExecutor> =
        listOf(
            IdentifierExpressionExecutor(),
            LiteralExpressionExecutor(),
        )

    private fun getExpressionExecutors(
        inputProvider: InputProvider,
    ): List<SpecificExpressionExecutor> =
        listOf(
            BinaryExpressionExecutor(expressions = identifierAndLiteralExecutors),
            IdentifierExpressionExecutor(),
            LiteralExpressionExecutor(),
            ReadInputExpressionExecutor(inputProvider),
            ReadEnvExpressionExecutor(EnvInputProvider()),
        )

    private val coercers: TypeCoercer =
        TypeCoercer(
            listOf(
                StringToNumberConverter(),
                StringToBooleanConverter(),
                StringToStringConverter(),
            ),
        )

    fun getStatements(
        coercers: TypeCoercer,
        allSpecificExpressionExecutors: List<SpecificExpressionExecutor>,
        emitter: Emitter,
    ): DefaultStatementExecutor {
        dataBase.clear()

        val expressionExecutor = DefaultExpressionExecutor(allSpecificExpressionExecutors)

        val statementSpecialists = mutableListOf<SpecificStatementExecutor>()
        val mainStatementExecutor = DefaultStatementExecutor(statementSpecialists)

        val constDeclaration =
            ConstDeclarationStatementExecutor(dataBase, expressionExecutor, coercers)
        val letDeclaration = LetDeclarationStatement(expressionExecutor, coercers)

        val declarationExecutor =
            DeclarationStatementExecutor(
                expressionExecutor,
                listOf(constDeclaration, letDeclaration),
            )

        val assignmentExecutor = AssignmentStatementExecutor(expressionExecutor)
        val printExecutor = PrintStatementExecutor(expressionExecutor, emitter)
        val expressionStatementExecutor = ExpressionStatementExecutor(expressionExecutor)
        val ifExecutor = IfStatementExecutor(expressionExecutor, mainStatementExecutor)

        statementSpecialists.add(declarationExecutor)
        statementSpecialists.add(assignmentExecutor)
        statementSpecialists.add(printExecutor)
        statementSpecialists.add(ifExecutor)
        statementSpecialists.add(expressionStatementExecutor)

        return mainStatementExecutor
    }

    fun createDefaultInterpreter(
        emitter: Emitter = PrintEmitter(),
        inputProvider: InputProvider = TerminalInputProvider(),
    ): DefaultInterpreter {
        val expressionExecutors = getExpressionExecutors(inputProvider)
        return DefaultInterpreter(
            dataBase,
            DefaultExpressionExecutor(expressionExecutors),
            getStatements(coercers, expressionExecutors, emitter),
        )
    }
}
