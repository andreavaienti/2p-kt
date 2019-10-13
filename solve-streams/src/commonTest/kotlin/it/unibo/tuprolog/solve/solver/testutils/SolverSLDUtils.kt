package it.unibo.tuprolog.solve.solver.testutils

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.libraries.stdlib.Conjunction
import it.unibo.tuprolog.libraries.stdlib.Cut
import it.unibo.tuprolog.libraries.stdlib.testutils.CallUtils
import it.unibo.tuprolog.libraries.stdlib.testutils.CatchUtils
import it.unibo.tuprolog.libraries.stdlib.testutils.HaltUtils
import it.unibo.tuprolog.libraries.stdlib.testutils.ThrowUtils
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.SolverSLD
import it.unibo.tuprolog.solve.testutils.DummyInstances
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.collections.listOf as ktListOf

/**
 * Utils singleton to help testing [SolverSLD]
 *
 * @author Enrico
 */
internal object SolverSLDUtils {

    /**
     * The clause database used in Prolog Standard reference manual, when explaining solver functionality
     *
     * ```prolog
     * p(X, Y) :- q(X), r(X, Y).
     * p(X, Y) :- s(X).
     * s(d).
     * q(a).
     * q(b).
     * q(c).
     * r(b, b1).
     * r(c, c1).
     * ```
     */
    private val prologStandardExampleDatabase by lazy {
        ClauseDatabase.of(
                {
                    ruleOf(structOf("p", varOf("X"), varOf("Y")),
                            structOf("q", varOf("X")),
                            structOf("r", varOf("X"), varOf("Y"))
                    )
                },
                {
                    ruleOf(structOf("p", varOf("X"), varOf("Y")),
                            structOf("s", varOf("X")))
                },
                { factOf(structOf("s", atomOf("d"))) },
                { factOf(structOf("q", atomOf("a"))) },
                { factOf(structOf("q", atomOf("b"))) },
                { factOf(structOf("q", atomOf("c"))) },
                { factOf(structOf("r", atomOf("b"), atomOf("b1"))) },
                { factOf(structOf("r", atomOf("c"), atomOf("c1"))) }
        )
    }

    /**
     * Same as [prologStandardExampleDatabase] but first clause contains cut
     *
     * ```prolog
     * p(X, Y) :- q(X), !, r(X, Y).
     * p(X, Y) :- s(X).
     * s(d).
     * q(a).
     * q(b).
     * q(c).
     * r(b, b1).
     * r(c, c1).
     * ```
     */
    private val prologStandardExampleDatabaseWithCut by lazy {
        ClauseDatabase.of(
                Scope.empty {
                    ruleOf(structOf("p", varOf("X"), varOf("Y")),
                            structOf("q", varOf("X")),
                            atomOf("!"),
                            structOf("r", varOf("X"), varOf("Y"))
                    )
                },
                *prologStandardExampleDatabase.clauses.drop(1).toTypedArray()
        )
    }

    /**
     * A database that exercises cut functionality
     *
     * ```prolog
     * p(X, Y) :- q(X), !, r(Y).
     * p(X, Y) :- s(X).
     * s(d).
     * q(a).
     * q(b).
     * q(c).
     * r(b1).
     * r(c1).
     * ```
     */
    private val cutTestingDatabase by lazy {
        ClauseDatabase.of(
                {
                    ruleOf(structOf("p", varOf("X"), varOf("Y")),
                            structOf("q", varOf("X")),
                            atomOf("!"),
                            structOf("r", varOf("Y"))
                    )
                },
                {
                    ruleOf(structOf("p", varOf("X"), varOf("Y")),
                            structOf("s", varOf("X")))
                },
                { factOf(structOf("s", atomOf("d"))) },
                { factOf(structOf("q", atomOf("a"))) },
                { factOf(structOf("q", atomOf("b"))) },
                { factOf(structOf("q", atomOf("c"))) },
                { factOf(structOf("r", atomOf("b1"))) },
                { factOf(structOf("r", atomOf("c1"))) }
        )
    }

    /**
     * A database that should test backtracking functionality
     *
     * ```prolog
     * my_reverse(L1,L2) :- my_rev(L1,L2,[]).
     *
     * my_rev([],L2,L2) :- !.
     * my_rev([X|Xs],L2,Acc) :- my_rev(Xs,L2,[X|Acc]).
     * ```
     */
    private val backtrackingTestDatabase by lazy {
        ClauseDatabase.of(
                {
                    ruleOf(
                            structOf("my_reverse", varOf("L1"), varOf("L2")),
                            structOf("my_rev", varOf("L1"), varOf("L2"), listOf())
                    )
                },
                {
                    ruleOf(
                            structOf("my_rev", listOf(), varOf("L2"), varOf("L2")),
                            atomOf("!")
                    )
                },
                {
                    ruleOf(
                            structOf("my_rev", listFrom(ktListOf(varOf("X")), varOf("Xs")), varOf("L2"), varOf("Acc")),
                            structOf("my_rev", varOf("Xs"), varOf("L2"), listFrom(ktListOf(varOf("X")), varOf("Acc")))
                    )
                }
        )
    }

    /** Contains context and goal requests to be launched with solver, and corresponding expected solutions */
    internal val contextAndRequestToSolutionMap by lazy {
        Scope.empty {
            mapOf(
                    structOf("p", varOf("U"), varOf("V")).let {
                        (it to DummyInstances.executionContextImpl.copy(
                                libraries = Libraries(Library.of(
                                        alias = "testLib",
                                        theory = prologStandardExampleDatabase,
                                        primitives = mapOf(Conjunction.descriptionPair)
                                ))
                        )) to ktListOf(
                                Solution.No(it),
                                Solution.Yes(it, Substitution.of(
                                        varOf("U") to atomOf("b"),
                                        varOf("V") to atomOf("b1")
                                ) as Substitution.Unifier),
                                Solution.Yes(it, Substitution.of(
                                        varOf("U") to atomOf("c"),
                                        varOf("V") to atomOf("c1")
                                ) as Substitution.Unifier),
                                Solution.Yes(it, Substitution.of(
                                        varOf("U") to atomOf("d"),
                                        varOf("V") to whatever()
                                ) as Substitution.Unifier)
                        )
                    },
                    structOf("p", varOf("U"), varOf("V")).let {
                        (it to DummyInstances.executionContextImpl.copy(
                                libraries = Libraries(Library.of(
                                        alias = "testLib",
                                        theory = prologStandardExampleDatabaseWithCut,
                                        primitives = mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
                                ))
                        )) to ktListOf(Solution.No(it))
                    },
                    structOf("p", varOf("U"), varOf("V")).let {
                        (it to DummyInstances.executionContextImpl.copy(
                                libraries = Libraries(Library.of(
                                        alias = "testLib",
                                        theory = cutTestingDatabase,
                                        primitives = mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
                                ))
                        )) to ktListOf(
                                Solution.Yes(it, Substitution.of(
                                        varOf("U") to atomOf("a"),
                                        varOf("V") to atomOf("b1")
                                ) as Substitution.Unifier),
                                Solution.Yes(it, Substitution.of(
                                        varOf("U") to atomOf("a"),
                                        varOf("V") to atomOf("c1")
                                ) as Substitution.Unifier)
                        )
                    },
                    structOf("my_reverse", listOf((1..4).map(::numOf)), varOf("L")).let {
                        (it to DummyInstances.executionContextImpl.copy(
                                libraries = Libraries(Library.of(
                                        alias = "testLib",
                                        theory = backtrackingTestDatabase,
                                        primitives = mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
                                ))
                        )) to ktListOf(
                                Solution.Yes(it, Substitution.of(
                                        varOf("L") to listOf((1..4).reversed().map(::numOf))
                                ) as Substitution.Unifier)
                        )
                    },
                    *extractQueryContextSolutionPairs(HaltUtils.requestSolutionMap).toTypedArray(),
                    *extractQueryContextSolutionPairs(CallUtils.requestSolutionMap).toTypedArray(),
                    *extractQueryContextSolutionPairs(CallUtils.requestToErrorSolutionMap).toTypedArray(),
                    *extractQueryContextSolutionPairs(ThrowUtils.requestSolutionMap).toTypedArray(),
                    *extractQueryContextSolutionPairs(CatchUtils.requestSolutionMap).toTypedArray(),
                    *extractQueryContextSolutionPairs(CatchUtils.prologStandardCatchExamples).toTypedArray(),
                    *extractQueryContextSolutionPairs(CatchUtils.prologStandardThrowExamples).toTypedArray(),
                    *extractQueryContextSolutionPairs(CatchUtils.prologStandardThrowExamplesWithError).toTypedArray()
            )
        }
    }

    /** An utility method to convert (request, solution list) format, to ((query, context), solution list) one */
    private fun extractQueryContextSolutionPairs(requestSolutionMap: Map<Solve.Request<ExecutionContextImpl>, Iterable<Solution>>) =
            requestSolutionMap.mapKeys { it.key.query to it.key.context }.entries.map { it.toPair() }

    /** Utility method to check if given solutions match */
    internal fun assertSolutionsCorrect(expected: Iterable<Solution>, actual: Iterable<Solution>) {
        assertEquals(expected.count(), actual.count(), "Expected: `${expected.toList()}` Actual: `${actual.toList()}`")

        expected.zip(actual).forEach { (expected, actual) ->
            assertEquals(expected::class, actual::class, "Expected: `$expected` Actual: `$actual`")
            assertEquals(expected.query, actual.query, "Expected: `$expected` Actual: `$actual`")
            assertEquals(expected.substitution.count(), actual.substitution.count(), "Expected: `$expected` Actual: `$actual`")

            val actualVarScope = Scope.of(*actual.substitution.keys.toTypedArray())
            expected.substitution.forEach { (varExpected, termExpected) ->
                actual.substitution[actualVarScope.varOf(varExpected.name)].let {
                    assertNotNull(it)
                    assertTrue("Expected: `$termExpected` Actual: `$it`") { it.structurallyEquals(termExpected) }
                }
            }
        }
    }

}