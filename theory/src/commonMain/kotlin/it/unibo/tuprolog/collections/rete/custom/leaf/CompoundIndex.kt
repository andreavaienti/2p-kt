package it.unibo.tuprolog.collections.rete.custom.leaf

import it.unibo.tuprolog.collections.rete.custom.IndexingNode
import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.Utils.functorOfNestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.Utils.nestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.collections.rete.custom.nodes.FunctorIndexing
import it.unibo.tuprolog.collections.rete.custom.nodes.FunctorNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.dequeOf

internal class CompoundIndex(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : IndexingNode {

    private val functors: MutableMap<String, FunctorIndexing> = mutableMapOf()
    private val theoryCache: Cached<MutableList<SituatedIndexedClause>> = Cached.of(this::regenerateCache)

    override fun get(clause: Clause): Sequence<Clause> =
        if (clause.isGlobal()) {
            if (ordered) {
                Utils.merge(
                    functors.values.asSequence().flatMap {
                        it.getIndexed(clause)
                    }
                ).map { it.innerClause }
            } else {
                Utils.flatten(
                    functors.values.asSequence().flatMap {
                        it.get(clause)
                    }
                )
            }
        } else {
            functors[clause.nestedFunctor()]?.get(clause) ?: emptySequence()
        }

    override fun assertA(clause: IndexedClause) =
        clause.nestedFunctor().let {
            if (ordered) {
                functors.getOrPut(it) {
                    FunctorNode.FunctorIndexingNode(ordered, nestingLevel)
                }.assertA(clause)
            } else {
                assertZ(clause)
            }
        }

    override fun assertZ(clause: IndexedClause) =
        clause.nestedFunctor().let {
            functors.getOrPut(it) {
                FunctorNode.FunctorIndexingNode(ordered, nestingLevel)
            }.assertZ(clause)
        }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        if (ordered) {
            retractAllOrdered(clause).invalidatingCacheIfNonEmpty()
        } else {
            retractAllUnordered(clause).invalidatingCacheIfNonEmpty()
        }


    override fun getCache(): Sequence<SituatedIndexedClause> =
        theoryCache.value.asSequence()

    private fun retractAllOrdered(clause: Clause): Sequence<Clause> =
        if (clause.isGlobal()) {
            Utils.merge(
                functors.values.map {
                    it.retractAllIndexed(clause).invalidatingCacheIfNonEmpty()
                }
            ).map { it.innerClause }
        } else {
            functors[clause.nestedFunctor()]
                ?.retractAll(clause)?.invalidatingCacheIfNonEmpty()
                ?: emptySequence()
        }

    private fun retractAllUnordered(clause: Clause): Sequence<Clause> =
        if (clause.isGlobal()) {
            Utils.flatten(
                functors.values.map {
                    it.retractAll(clause).invalidatingCacheIfNonEmpty()
                }
            )
        } else {
            functors[clause.nestedFunctor()]
                ?.retractAll(clause)?.invalidatingCacheIfNonEmpty()
                ?: emptySequence()
        }

    override fun getFirstIndexed(clause: Clause): SituatedIndexedClause? =
        if (clause.isGlobal()) {
            Utils.merge(
                sequenceOf(
                    functors.values.mapNotNull {
                        it.getFirstIndexed(clause)
                    }.asSequence()
                )
            ).firstOrNull()
        } else {
            functors[clause.nestedFunctor()]
                ?.getFirstIndexed(clause)
        }

    override fun getIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
        if (clause.isGlobal()) {
            Utils.merge(
                functors.values.map {
                    it.getIndexed(clause)
                }
            )
        } else {
            functors[clause.nestedFunctor()]
                ?.getIndexed(clause)
                ?: emptySequence()
        }

    override fun retractAllIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
        if (clause.isGlobal()) {
            Utils.merge(
                functors.values.map {
                    it.retractAllIndexed(clause).invalidatingCacheIfNonEmpty()
                }
            )
        } else {
            functors[clause.nestedFunctor()]
                ?.retractAllIndexed(clause)?.invalidatingCacheIfNonEmpty()
                ?: emptySequence()
        }

    override fun extractGlobalIndexedSequence(clause: Clause): Sequence<SituatedIndexedClause> {
        return if (ordered)
            Utils.merge(
                functors.values.map {
                    it.extractGlobalIndexedSequence(clause)
                }
            )
        else
            Utils.flattenIndexed(
                functors.values.map {
                    it.extractGlobalIndexedSequence(clause)
                }
            )
    }

    private fun Clause.nestedFunctor(): String =
        this.head!!.functorOfNestedFirstArgument(nestingLevel)

    private fun IndexedClause.nestedFunctor(): String =
        this.innerClause.head!!.functorOfNestedFirstArgument(nestingLevel)

    private fun Clause.isGlobal(): Boolean =
        this.head!!.nestedFirstArgument(nestingLevel) is Var

    override fun invalidateCache() {
        theoryCache.invalidate()
//        functors.values.forEach { it.invalidateCache() }
    }

    private fun regenerateCache(): MutableList<SituatedIndexedClause> =
        dequeOf(
            if (ordered) {
                Utils.merge(
                    functors.values.map {
                        it.getCache()
                    }
                )
            } else {
                Utils.flattenIndexed(
                    functors.values.map { outer ->
                        outer.getCache()
                    }
                )
            }
        )

}