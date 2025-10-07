package factory

import DefaultInterpreter
import Interpreter
import data.DataBase
import emitter.Emitter
import executor.expression.SpecificExpressionExecutor
import executor.statement.SpecificStatementExecutor
import input.InputProvider
import type.Version

object DefaultInterpreterFactory : InterpreterFactory {

    override fun createInterpreter(version: Version): Interpreter =
        when (version) {
            Version.VERSION_1_0 -> InterpreterFactoryVersionOne.createDefaultInterpreter()
            Version.VERSION_1_1 -> InterpreterFactoryVersionOnePointOne.createDefaultInterpreter()
        }

    override fun createCustomInterpreter(
        specificExpressionExecutors: List<SpecificExpressionExecutor>,
        specificStatementExecutor: List<SpecificStatementExecutor>,
        emitter: Emitter,
        inputProvider: InputProvider,
        database: DataBase,
    ): DefaultInterpreter =
        DefaultInterpreter(
            initialDatabase = database,
            executor.expression.DefaultExpressionExecutor(specificExpressionExecutors),
            executor.statement.DefaultStatementExecutor(specificStatementExecutor),
        )

    override fun createDefaultInterpreter(): Interpreter =
        InterpreterFactoryVersionOne.createDefaultInterpreter()

    fun createWithVersionAndEmitterAndInputProvider(
        version: Version,
        emitter: Emitter,
        inputProvider: InputProvider,
    ): Interpreter =
        when (version) {
            Version.VERSION_1_0 -> InterpreterFactoryVersionOne.createDefaultInterpreter(emitter)
            Version.VERSION_1_1 ->
                InterpreterFactoryVersionOnePointOne.createDefaultInterpreter(
                    emitter,
                    inputProvider,
                )
        }
}
