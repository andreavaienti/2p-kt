package it.unibo.tuprolog.theory.rete2.clause

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.theory.rete2.AbstractLeafReteNode
import it.unibo.tuprolog.unify.Unificator.Companion.matches

/** A leaf node containing [Rule]s */
internal data class RuleNode(override val leafElements: MutableList<Rule> = mutableListOf()) :
    AbstractLeafReteNode<Rule>() {

    override val header = "Rules"

    override fun get(element: Rule): Sequence<Rule> = indexedElements.filter { it matches element }

    override fun deepCopy(): RuleNode = RuleNode(leafElements.toMutableList())
}
