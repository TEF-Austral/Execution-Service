import data.DataBase
import data.DefaultDataBase
import executor.expression.DefaultExpressionExecutor
import executor.statement.DefaultStatementExecutor
import node.Expression
import node.Statement
import node.ASTNode
import result.InterpreterResult
import stream.AstStream
import variable.Variable

class DefaultInterpreter(
    private val initialDatabase: DataBase = DefaultDataBase(),
    private val expressionExecutor: DefaultExpressionExecutor,
    private val statementExecutor: DefaultStatementExecutor,
) : Interpreter {

    override fun interpret(stream: AstStream): InterpreterResult =
        try {
            executeStream(stream, initialDatabase, null)
        } catch (e: Error) {
            InterpreterResult(false, "${e.message}", null)
        }

    private tailrec fun executeStream(
        currentStream: AstStream,
        currentDatabase: DataBase,
        lastVariable: Variable?,
    ): InterpreterResult {
        if (currentStream.isAtEnd()) {
            return InterpreterResult(
                true,
                "Program executed successfully",
                lastVariable,
                currentDatabase,
            )
        }

        val streamResult = currentStream.next()
        val node = streamResult.node
        val nextStream = streamResult.nextStream

        val executionResult =
            evaluateNode(node, currentDatabase, statementExecutor, expressionExecutor)

        if (!executionResult.interpretedCorrectly) {
            return executionResult
        }

        val newDatabase = executionResult.updatedDatabase ?: currentDatabase
        val newVariable = executionResult.interpreter

        return executeStream(nextStream, newDatabase, newVariable)
    }
}

private fun evaluateNode(
    node: ASTNode,
    currentDatabase: DataBase,
    statementExecutor: DefaultStatementExecutor,
    expressionExecutor: DefaultExpressionExecutor,
): InterpreterResult =
    when (node) {
        is Statement -> statementExecutor.execute(node, currentDatabase)
        is Expression -> expressionExecutor.execute(node, currentDatabase)
        else ->
            InterpreterResult(
                false,
                "Unsupported node type in stream: ${node.javaClass.simpleName}",
                null,
                currentDatabase,
            )
    }
