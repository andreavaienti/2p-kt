package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

/**
 * Prolog error handling utilities
 *
 * @author Enrico
 */
object ErrorUtils {

    /** The functor `error` used to wrap errors generated by built-ins */
    const val errorWrapperFunctor = "error"

    /**
     * The error struct, with [errorWrapperFunctor], that can be used in `throw/1` predicate; It will create a
     * struct in the form of `error(`[errorDescription], [customErrorData]`)`
     *
     * For example if the [errorDescription] is `instantiation_error` the corresponding error struct, according to prolog standard, will be
     * `error(instantiation_error, `[customErrorData]`)`
     *
     * If the error [errorDescription] is composite like `type_error(callable, Goal)` the corresponding struct, according to prolog standard, will be
     * `error(type_error(callable, Goal), `[customErrorData]`)`
     */
    fun errorStructOf(errorDescription: Struct, customErrorData: Term = Atom.of("")) =
        Struct.of(errorWrapperFunctor, errorDescription, customErrorData)

}