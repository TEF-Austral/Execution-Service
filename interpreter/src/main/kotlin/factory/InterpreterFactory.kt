package factory

import DefaultInterpreter
import Interpreter
import data.DataBase
import emitter.Emitter
import executor.expression.SpecificExpressionExecutor
import executor.statement.SpecificStatementExecutor
import input.InputProvider
import type.Version

interface InterpreterFactory {

    fun createInterpreter(version: Version): Interpreter

    fun createCustomInterpreter(
        specificExpressionExecutors: List<SpecificExpressionExecutor>,
        specificStatementExecutor: List<SpecificStatementExecutor>,
        emitter: Emitter,
        inputProvider: InputProvider,
        database: DataBase,
    ): DefaultInterpreter

    fun createDefaultInterpreter(): Interpreter
}
