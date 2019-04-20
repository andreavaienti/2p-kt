package it.unibo.tuprolog.core

import kotlin.collections.List

internal open class StructImpl(override val functor: String, override val args: Array<Term>) : TermImpl(), Struct {

    override val isFunctorWellFormed: Boolean by lazy {
        Struct.WELL_FORMED_FUNCTOR_PATTERN.matches(functor)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        if (functor != (other as StructImpl).functor) return false
        if (!args.contentEquals(other.args)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = functor.hashCode()
        result = 31 * result + args.contentHashCode()
        return result
    }

    override fun toString(): String {
        return (
                    if (isFunctorWellFormed) functor else "'$functor'"
                ) + (
                    if (arity > 0) "(${args.joinToString(", ")})" else ""
                )
    }

    override val isGround: Boolean by lazy { super<Struct>.isGround }

    override val argsList: List<Term> by lazy { super.argsList }

    override val argsSequence: Sequence<Term> by lazy { super.argsSequence }

}