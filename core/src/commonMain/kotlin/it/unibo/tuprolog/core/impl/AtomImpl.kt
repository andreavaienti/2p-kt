package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term

internal open class AtomImpl(override val functor: String) : StructImpl(functor, arrayOf()), Atom {

    override val args: Array<Term> = super<StructImpl>.args

    // TODO: 29/06/2019 missing structurallyEquals override??

    override fun strictlyEquals(other: Term): Boolean =
            other is AtomImpl
                    && value == other.value

    override val argsList: List<Term> by lazy { emptyList<Term>() }

    override val isGround: Boolean
        get() = super<Atom>.isGround
}
