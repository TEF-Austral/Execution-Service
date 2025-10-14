package executor.expression

import data.DataBase
import input.EnvInputProvider
import input.InputProvider
import node.Expression
import node.ReadEnvExpression
import result.InterpreterResult

class ReadEnvExpressionExecutor(
    private val inputProvider: InputProvider = EnvInputProvider(),
) : SpecificExpressionExecutor {
    override fun canHandle(expression: Expression): Boolean = expression is ReadEnvExpression

    override fun execute(
        expression: Expression,
        database: DataBase,
    ): InterpreterResult {
        val readEnvExpression = expression as ReadEnvExpression
        val envName = readEnvExpression.envName()

        val envResult = inputProvider.input(envName)

        return if (envResult.interpretedCorrectly) {
            envResult
        } else {
            InterpreterResult(
                false,
                "Error: Environment variable '$envName' not found.",
                null,
            )
        }
    }
}
