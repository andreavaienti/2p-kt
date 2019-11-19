package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.prologStandardExampleDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.haltException
import kotlin.collections.listOf as ktListOf

/**
 * An object containing the collection of Prolog Standard databases and requests, testing ISO functionality
 *
 * @author Enrico
 */
object PrologStandardExampleDatabases {

    /**
     * The clause database used in Prolog Standard reference manual, when explaining solver functionality and search-trees
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
    val prologStandardExampleDatabase by lazy {
        prolog {
            theory(
                { "p"("X", "Y") `if` ("q"("X") and "r"("X", "Y")) },
                { "p"("X", "Y") `if` "s"("X") },
                { "s"("d") },
                { "q"("a") },
                { "q"("b") },
                { "q"("c") },
                { "r"("b", "b1") },
                { "r"("c", "c1") }
            )
        }
    }

    /**
     * Notable [prologStandardExampleDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- p(U, V).
     * ```
     */
    val prologStandardExampleDatabaseNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                "p"("U", "V").hasSolutions(
                    { yes("U" to "b", "V" to "b1") },
                    { yes("U" to "c", "V" to "c1") },
                    { yes("U" to "d", "V" to "Y") }
                )
            )
        }
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
    val prologStandardExampleWithCutDatabase by lazy {
        prolog {
            theory({ "p"("X", "Y") `if` ("q"("X") and "!" and "r"("X", "Y")) }) +
                    theoryOf(*prologStandardExampleDatabase.clauses.drop(1).toTypedArray())
        }
    }

    /**
     * Notable [prologStandardExampleWithCutDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- p(U, V).
     * ```
     */
    val prologStandardExampleWithCutDatabaseNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                "p"("U", "V").hasSolutions({ no() })
            )
        }
    }

    /**
     * The database used in Prolog standard while writing examples for Conjunction
     * ```prolog
     * legs(A, 6) :- insect(A).
     * legs(A, 4) :- animal(A).
     * insect(bee).
     * insect(ant).
     * fly(bee).
     * ```
     */
    val conjunctionStandardExampleDatabase by lazy {
        prolog {
            theory(
                { "legs"("A", 6) `if` "insect"("A") },
                { "legs"("A", 4) `if` "animal"("A") },
                { "insect"("bee") },
                { "insect"("ant") },
                { "fly"("bee") }
            )
        }
    }

    /**
     * Notable [conjunctionStandardExampleDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- (insect(X) ; legs(X, 6)) , fly(X).
     * ```
     */
    val conjunctionStandardExampleDatabaseNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                (("insect"("X") or "legs"("X", 6)) and "fly"("X")).hasSolutions(
                    { yes("X" to "bee") },
                    { yes("X" to "bee") },
                    { no() }
                )
            )
        }
    }

    /**
     * The database used in Prolog standard while writing examples for Call
     * ```prolog
     * a(1).
     * a(2).
     * ```
     */
    val callStandardExampleDatabase by lazy {
        prolog {
            theory(
                { "a"(1) },
                { "a"(2) }
            )
        }
    }

    /**
     * Prolog Standard examples to test call primitive with [callStandardExampleDatabase]
     * ```prolog
     * ?- call('!') ; true.
     * ?- Z = !, call( (Z = !, a(X), Z) ).
     * ?- call( (Z = !, a(X), Z) ).
     * ?- call(fail).
     * ?- call(true, X).
     * ?- call(true, fail, 1).
     * ```
     */
    val callStandardExampleDatabaseGoalsToSolution by lazy {
        prolog {
            ktListOf(
                ("call"("!") or true).hasSolutions({ yes() }, { yes() }),
                ("="("Z", "!") and "call"("="("Z", "!") and "a"("X") and "Z")).hasSolutions(
                    { yes("X" to 1, "Z" to "!") }
                ),
                "call"("="("Z", "!") and "a"("X") and "Z").hasSolutions(
                    { yes("X" to 1, "Z" to "!") },
                    { yes("X" to 2, "Z" to "!") }
                ),
                "call"(false).hasSolutions({ no() }),
                "call"(true and "X").hasSolutions({ halt(haltException) }),
                "call"("true" and "false" and 1).hasSolutions({ halt(haltException) })
            )
        }
    }

    /**
     * The database used in Prolog standard while writing examples for Catch
     * ```prolog
     * p.
     * p :- throw(b).
     * r(X) :- throw(X).
     * q :- catch(p, B, true), r(c).
     * ```
     */
    val catchAndThrowStandardExampleDatabase by lazy {
        prolog {
            theory(
                { "p" },
                { "p" `if` "throw"("b") },
                { "r"("X") `if` "throw"("X") },
                { "q" `if` ("catch"("p", "B", true) and "r"("c")) }
            )
        }
    }

    /**
     * Notable [catchAndThrowStandardExampleDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- catch(p, X, true).
     * ?- catch(q, C, true).
     * ?- catch(throw(exit(1)), exit(X), true).
     * ?- catch(throw(true), X, X).
     * ?- catch(throw(fail), X, X).
     * ?- catch(throw(f(X, X)), f(X, g(X)), true).
     * ?- catch(throw(1), X, (fail; X)).
     * ?- catch(throw(fail), true, G).
     * ```
     */
    val catchAndThrowStandardExampleDatabaseNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                "catch"("p", "X", true).hasSolutions(
                    { yes() },
                    { yes("X" to "b") }
                ),
                "catch"("q", "C", true).hasSolutions({ yes("C" to "c") }),
                "catch"("throw"("exit"(1)), "exit"("X"), true).hasSolutions({ yes("X" to 1) }),
                "catch"("throw"(true), "X", "X").hasSolutions({ yes("X" to true) }),
                "catch"("throw"(false), "X", "X").hasSolutions({ no() }),
                "catch"("throw"("f"("X", "X")), "f"("X", "g"("X")), true).hasSolutions({ halt(haltException) }),
                "catch"("throw"(1), "X", false or "X").hasSolutions({ halt(haltException) }),
                "catch"("throw"(false), true, "G").hasSolutions({ halt(haltException) })
            )
        }
    }

    /** Collection of all Prolog Standard example databases and their respective callable goals with expected solutions */
    val allPrologStandardTestingDatabasesToRespectiveGoalsAndSolutions by lazy {
        mapOf(
            prologStandardExampleDatabase to prologStandardExampleDatabaseNotableGoalToSolution,
            prologStandardExampleWithCutDatabase to prologStandardExampleWithCutDatabaseNotableGoalToSolution,
            conjunctionStandardExampleDatabase to conjunctionStandardExampleDatabaseNotableGoalToSolution,
            callStandardExampleDatabase to callStandardExampleDatabaseGoalsToSolution,
            catchAndThrowStandardExampleDatabase to catchAndThrowStandardExampleDatabaseNotableGoalToSolution
        )
    }
}