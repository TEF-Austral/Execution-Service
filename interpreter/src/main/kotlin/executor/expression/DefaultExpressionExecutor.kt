package executor.expression

import data.DataBase
import result.InterpreterResult
import node.Expression

class DefaultExpressionExecutor(
    private val specificExecutors: List<SpecificExpressionExecutor>,
) : ExpressionExecutor {
    override fun execute(
        expression: Expression,
        database: DataBase,
    ): InterpreterResult {
        for (executor in specificExecutors) {
            if (executor.canHandle(expression)) {
                return executor.execute(expression, database)
            }
        }
        return InterpreterResult(false, "No executor found for the given expression", null)
    }
}
