package it.unibo.tuprolog.theory.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.ClauseDatabase

/**
 * Utils singleton for testing [ClauseDatabase]
 *
 * @author Enrico
 */
internal object ClauseDatabaseUtils {

    /** Contains well formed clauses that will need to be rewritten, because they contain variables in body top level */
    internal val toBeRewrittenWellFormedClauses by lazy {
        listOf(
                Clause.of(Atom.of("a"), Var.of("A"), Var.of("A")), // TODO re-enable this
                Rule.of(Struct.of("f", Atom.of("a")), Var.of("Variable"))
        )
    }

    /** Contains well formed clauses (the head is a [Struct] and the body doesn't contain [Numeric] values) */
    internal val wellFormedClauses by lazy {
        listOf(
                Clause.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())),
                Clause.of(Struct.of("p", Atom.of("john"))),
                Directive.of(Atom.of("execute_this")),
                Rule.of(Struct.of("f", Atom.of("a")), Atom.of("do_something")),
                Rule.of(Struct.of("f", Atom.of("a"), Struct.of("b", Var.of("X")), Atom.of("do_something_else"))),
                Fact.of(Struct.of("g", Struct.of("c", Var.anonymous(), Var.anonymous())))
        ) + toBeRewrittenWellFormedClauses
    }

    /** Contains not well formed clauses (with [Numeric] values in body) */
    internal val notWellFormedClauses by lazy {
        listOf(
                Clause.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous()), Integer.of(1)),
                Directive.of(Atom.of("execute_this"), Real.of(1.5)),
                Rule.of(Struct.of("f", Atom.of("a")), Atom.of("do_something"), Numeric.of(1.5f))
        )
    }

}