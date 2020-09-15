package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.PrologError

/**
 * The instantiation error occurs when some Term is a Variable, and it should not
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 * @param extraData The possible extra data to be carried with the error
 *
 * @author Enrico
 */
class InstantiationError(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
    val culprit: Var = Var.anonymous(),
    extraData: Term? = null
) : PrologError(message, cause, contexts, Atom.of(typeFunctor), extraData) {

    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        culprit: Var = Var.anonymous(),
        extraData: Term? = null
    ) : this(message, cause, arrayOf(context), culprit, extraData)

    override fun updateContext(newContext: ExecutionContext): InstantiationError =
        InstantiationError(message, cause, contexts.setFirst(newContext), culprit, extraData)

    override fun pushContext(newContext: ExecutionContext): InstantiationError =
        InstantiationError(message, cause, contexts.addLast(newContext), culprit, extraData)

    companion object {

        /** The instantiation error Struct functor */
        const val typeFunctor = "instantiation_error"

        fun forArgument(context: ExecutionContext, procedure: Signature, variable: Var, index: Int? = null) =
            message(
                "${index?.let { "The $it-th argument" } ?: "The argument"} `${variable.pretty()}` " +
                    "of ${procedure.pretty()} is unexpectedly not instantiated"
            ) { m, extra ->
                InstantiationError(
                    message = m,
                    context = context,
                    culprit = variable,
                    extraData = extra
                )
            }

        fun forGoal(context: ExecutionContext, procedure: Signature, variable: Var) =
            message(
                "Uninstantiated subgoal ${variable.pretty()} in procedure ${procedure.pretty()}"
            ) { m, extra ->
                InstantiationError(
                    message = m,
                    context = context,
                    culprit = variable,
                    extraData = extra
                )
            }
    }
}
