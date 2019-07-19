package it.unibo.tuprolog.theory.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.ReteTree
import it.unibo.tuprolog.unify.Unification.Companion.matches
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Utils singleton for testing [ReteTree]
 *
 * @author Enrico
 */
internal object ReteTreeUtils {

    /** Contains some well-formed rules */
    internal val rules by lazy {
        listOf(
                Fact.of(Truth.`true`()),
                Fact.of(Truth.fail()),
                Fact.of(Atom.of("a")),
                Fact.of(Atom.of("other")),
                Fact.of(Struct.of("a", Atom.of("other"))),
                Fact.of(Struct.of("other", Integer.of(1))),
                Rule.of(Atom.of("a"), Atom.of("other")),
                Rule.of(Struct.of("a", Atom.of("other")), Atom.of("a")),
                Rule.of(Struct.of("f", Atom.of("a"), Struct.of("b", Var.of("X")), Atom.of("do_something_else"))),
                Rule.of(Struct.of("a", Integer.of(22)), Var.anonymous()),
                Rule.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
                Rule.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
                Rule.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())),
                Rule.of(Struct.of("a", Atom.of("a")), Empty.set()),
                Rule.of(Struct.of("a", Atom.of("a")), Struct.of("other", Var.anonymous())),
                Rule.of(Struct.of("a", Atom.of("a")), Struct.of("a", Var.anonymous())),
                Rule.of(Struct.of("a", Atom.of("a")), Var.anonymous())
        )
    }

    /** Contains a map of queries and results crafted watching [rules] collection (NOTE: any modifications must be reviewed by hand)*/
    internal val rulesQueryResultsMap by lazy {
        mapOf(
                Fact.of(Truth.`true`()) to listOf(Fact.of(Truth.`true`())),
                Fact.of(Empty.list()) to emptyList(),
                Rule.of(Struct.of("a", Atom.of("a")), Var.anonymous()) to rules.takeLast(5),
                Rule.of(Struct.of("a", Atom.of("other")), Var.anonymous()).run {
                    this to rules.filter { it matches this }
                },
                Rule.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())).run {
                    this to rules.filter { it matches this }
                }
        )
    }

    /** Asserts that rete node has correct elements count */
    internal fun assertReteNodeElementCount(reteNode: ReteTree<*>, expectedCount: Int) {
        assertEquals(expectedCount, reteNode.clauses.count())
    }

    /** Asserts that rete node is empty */
    internal fun assertReteNodeEmpty(reteNode: ReteTree<*>) {
        assertTrue(reteNode.clauses.none())
    }

    /** Asserts that rete node clauses are the same as expected */
    internal fun assertReteNodeClausesCorrect(reteNode: ReteTree<*>, expectedClauses: Iterable<Clause>) {
        assertEquals(expectedClauses.toList(), reteNode.clauses.toList())
    }

    /** Asserts that calling [idempotentAction] onto [reteNode] results in no actual change */
    internal fun assertNoChangesInReteNode(reteNode: ReteTree<*>, idempotentAction: ReteTree<*>.() -> Sequence<Clause>) {
        val beforeContents = reteNode.clauses.toList()

        val idempotentActionResult = reteNode.idempotentAction()

        assertTrue(idempotentActionResult.none())
        assertReteNodeClausesCorrect(reteNode, beforeContents)
    }

    /** Asserts that calling [removeAction] onto [reteNode] results in [removedExpected] elements removed */
    internal fun assertRemovedFromReteNode(reteNode: ReteTree<*>, removedExpected: Iterable<Clause>, removeAction: ReteTree<*>.() -> Sequence<Clause>) {
        val allRulesCount = reteNode.clauses.count()

        val removedActual = reteNode.removeAction()

        assertEquals(removedExpected, removedActual.toList())
        assertReteNodeElementCount(reteNode, allRulesCount - removedExpected.count())
    }
}
