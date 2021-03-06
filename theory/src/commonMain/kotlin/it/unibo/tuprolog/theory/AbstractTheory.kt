package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.TheoryUtils.checkClausesCorrect

internal abstract class AbstractTheory : Theory {

    override fun plus(clause: Clause): Theory = super.plus(TheoryUtils.checkClauseCorrect(clause))

    override fun contains(clause: Clause): Boolean = get(clause).any()

    override fun contains(head: Struct): Boolean = contains(Rule.of(head, Var.anonymous()))

    override fun contains(indicator: Indicator): Boolean = get(indicator).any()

    override fun get(head: Struct): Sequence<Rule> = get(Rule.of(head, Var.anonymous())).map { it as Rule }

    override fun get(indicator: Indicator): Sequence<Rule> {
        require(indicator.isWellFormed) { "The provided indicator is not well formed: $indicator" }

        return get(
            Rule.of(
                Struct.of(indicator.indicatedName!!, (1..indicator.indicatedArity!!).map { Var.anonymous() }),
                Var.anonymous()
            )
        ).map { it as Rule }
    }

    override fun abolish(indicator: Indicator): Theory {
        require(indicator.isWellFormed) { "The provided indicator is not well formed: $indicator" }

        return retractAll(Struct.template(indicator.indicatedName!!, indicator.indicatedArity!!)).theory
    }

    override fun toString(): String = "${Theory::class.simpleName}(clauses=$clauses)"

    override fun toString(asPrologText: Boolean): String = when (asPrologText) {
        true -> clauses.joinToString(".\n", "", ".\n")
        false -> toString()
    }

    override fun iterator(): Iterator<Clause> = clauses.iterator()

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (other !is Theory) return false

        val i = clauses.iterator()
        val j = other.clauses.iterator()

        while (i.hasNext() && j.hasNext()) {
            if (i.next() != j.next()) {
                return false
            }
        }

        return i.hasNext() == j.hasNext()
    }

    override fun hashCode(): Int {
        val base = AbstractTheory::class.simpleName.hashCode()
        var result = (base xor (base ushr 32))
        for (clause in clauses) {
            result = 31 * result + clause.hashCode()
        }
        return result
    }

    override val size: Long by lazy {
        var i: Long = 0
        for (clause in clauses) {
            i++
        }
        i
    }

    override fun plus(theory: Theory): Theory =
        createNewTheory(clauses.asSequence() + checkClausesCorrect(theory.clauses.asSequence()))

    override fun assertA(clause: Clause): Theory =
        createNewTheory(checkClausesCorrect(sequenceOf(clause)) + clauses.asSequence())

    override fun assertA(clauses: Iterable<Clause>): Theory =
        createNewTheory(checkClausesCorrect(clauses.asSequence()) + this.clauses.asSequence())

    override fun assertA(clauses: Sequence<Clause>): Theory =
        createNewTheory(checkClausesCorrect(clauses) + this.clauses.asSequence())

    override fun assertZ(clause: Clause): Theory =
        createNewTheory(clauses.asSequence() + checkClausesCorrect(sequenceOf(clause)))

    override fun assertZ(clauses: Iterable<Clause>): Theory =
        createNewTheory(this.clauses.asSequence() + checkClausesCorrect(clauses).asSequence())

    override fun assertZ(clauses: Sequence<Clause>): Theory =
        createNewTheory(this.clauses.asSequence() + checkClausesCorrect(clauses))

    protected abstract fun createNewTheory(clauses: Sequence<Clause>): AbstractTheory

    override fun retract(clauses: Sequence<Clause>): RetractResult =
        retract(clauses.asIterable())
}
