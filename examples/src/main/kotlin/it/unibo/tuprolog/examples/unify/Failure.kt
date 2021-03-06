package it.unibo.tuprolog.examples.unify

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator

fun main() {
    val unificator = Unificator.default

    val term = Struct.of("father", Atom.of("abraham"), Atom.of("isaac"))
    val template = Struct.of("father", Atom.of("isaac"), Atom.of("abraham"))

    val substitution: Substitution = unificator.mgu(term, template)
    val match: Boolean = unificator.match(term, template)
    val unified: Term? = unificator.unify(term, template)

    println(substitution is Substitution.Fail) // true
    println(substitution.isFailed) // true
    println(match) // false
    println(unified) // null
}
