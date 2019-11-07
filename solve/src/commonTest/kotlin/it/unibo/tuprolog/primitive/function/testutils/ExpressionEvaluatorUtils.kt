package it.unibo.tuprolog.primitive.function.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.function.ExpressionEvaluator
import it.unibo.tuprolog.primitive.function.PrologFunction
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.testutils.DummyInstances

/**
 * Utils singleton to help testing [ExpressionEvaluator]
 *
 * @author Enrico
 */
internal object ExpressionEvaluatorUtils {

    /** A context with empty functions map */
    internal val noFunctionsContext = object : ExecutionContext by DummyInstances.executionContext {
        override val libraries: Libraries = Libraries()
    }

    /** Test data is in the form (input, transforming function, expected output) */
    internal val inputFunctionOutputTriple by lazy {
        listOf<Triple<Term, PrologFunction, Term>>(
                Triple(
                        Atom.of("a"),
                        { request -> request.replyWith(Atom.of("b")) },
                        Atom.of("b")
                ),
                Triple(
                        Struct.of("extractAnotherTerm", Atom.of("b")),
                        { request -> with(request) { replyWith(Struct.of("resultTerm", arguments.single())) } },
                        Struct.of("resultTerm", Atom.of("b"))
                ),
                Triple(
                        Struct.of("concat", Atom.of("a"), Atom.of("b")),
                        { request -> with(request) { replyWith(Atom.of(arguments.first().toString() + arguments.last().toString())) } },
                        Atom.of("ab")
                ),
                Triple(
                        Struct.of("concat", Struct.of("concat", Atom.of("a"), Atom.of("b")), Struct.of("concat", Atom.of("a"), Atom.of("b"))),
                        { request -> with(request) { replyWith(Atom.of(arguments.first().toString() + arguments.last().toString())) } },
                        Atom.of("abab")
                )
        )
    }

    /** Creates a context with provided signature-function binding */
    internal fun createContextWithFunctionBy(signature: Signature, function: PrologFunction): ExecutionContext =
            object : ExecutionContext by DummyInstances.executionContext {
                override val libraries: Libraries = Libraries(Library.of(
                        alias = "test.expression.evaluator",
                        functions = mapOf(signature to function)
                ))
            }

}