package it.unibo.tuprolog.solve.solver.fsm.impl

import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.fsm.FinalState
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateRuleSelectionUtils.createRequest
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateRuleSelectionUtils.queryToMultipleMatchesDatabaseAndSubstitution
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateRuleSelectionUtils.queryToNoMatchesDatabaseMap
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateRuleSelectionUtils.queryToOneMatchFactDatabaseAndSubstitution
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateRuleSelectionUtils.queryToOneMatchRuleDatabaseAndSubstitution
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateUtils.assertCorrectQueryAndSubstitution
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateUtils.assertOnlyOneNextState
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateUtils.assertOverFilteredStateInstances
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateUtils.assertOverState
import kotlin.test.Test
import kotlin.collections.listOf as ktListOf

/**
 * Test class for [StateRuleSelection]
 *
 * @author Enrico
 */
internal class StateRuleSelectionTest {

    private val theQueryVariable = Var.of("V")

    /** A struct query in the form `f(V)` */
    private val theQuery = prolog { "f"(theQueryVariable) }

    /** A Solve.Request with three databases and three different facts, to test how they should be used/combined in searching */
    private val threeDBSolveRequest = Solve.Request(theQuery.extractSignature(), theQuery.argsList,
            ExecutionContextImpl(
                    libraries = Libraries(Library.of(
                            alias = "testLib",
                            theory = prolog { theory({ "f"("a") }) }
                    )),
                    staticKB = prolog { theory({ "f"("b") }) },
                    dynamicKB = prolog { theory({ "f"("c") }) }
            ))

    @Test
    fun noMatchingRulesFoundMakeItGoIntoFalseState() {
        queryToNoMatchesDatabaseMap.forEach { (queryStruct, noMatchesDB) ->
            val nextStates = StateRuleSelection(createRequest(queryStruct, noMatchesDB)).behave()

            assertOnlyOneNextState<StateEnd.False>(nextStates)
        }
    }

    @Test
    fun oneMatchingRuleFoundUnifiesCorrectlyAndGivesSolution() {
        queryToOneMatchFactDatabaseAndSubstitution.forEach { (queryStruct, oneMatchDB, expectedSubstitution) ->
            val nextStates = StateRuleSelection(createRequest(queryStruct, oneMatchDB)).behave().toList()

            assertOverState<StateEnd.True>(nextStates.last()) {
                it.solve.solution.assertCorrectQueryAndSubstitution(queryStruct, expectedSubstitution)
            }
        }
    }

    @Test
    fun oneMatchingRuleFoundExecutesTheRuleBodyAndFindsSolutions() {
        queryToOneMatchRuleDatabaseAndSubstitution.forEach { (queryStruct, oneMatchDB, expectedSubstitution) ->
            val nextStates = StateRuleSelection(createRequest(queryStruct, oneMatchDB)).behave().toList()

            assertOverState<StateEnd>(nextStates.last()) {
                it.solve.solution.assertCorrectQueryAndSubstitution(queryStruct, expectedSubstitution)
            }
        }
    }

    @Test
    fun stateRuleSelectionFindsCorrectlyMultipleSolutions() {
        queryToMultipleMatchesDatabaseAndSubstitution.forEach { (queryStruct, oneMatchDB, expectedSubstitution) ->
            val nextStates = StateRuleSelection(createRequest(queryStruct, oneMatchDB)).behave()

            assertOverFilteredStateInstances<FinalState>(nextStates) { index, finalState ->
                assertOverState<StateEnd>(finalState) {
                    it.solve.solution.assertCorrectQueryAndSubstitution(queryStruct, expectedSubstitution[index])
                }
            }
        }
    }

    @Test
    fun stateRuleSelectionUsesFirstlyLibraryTheoryIfMatchesFoundAndNotOthers() {
        val nextStates = StateRuleSelection(threeDBSolveRequest).behave().toList()

        assertOverState<StateEnd.True>(nextStates.last()) {
            it.solve.solution.assertCorrectQueryAndSubstitution(theQuery, prolog { theQueryVariable to "a" })
        }
    }

    @Test
    fun stateRuleSelectionUsesCombinationOfStaticAndDynamicKBWhenLibraryTheoriesDoesntProvideMatches() {
        val dynamicAndStaticKBSolveRequest = with(threeDBSolveRequest) { copy(context = context.copy(libraries = Libraries())) }
        val correctSubstitutions = prolog {
            ktListOf(
                    theQueryVariable to "b",
                    theQueryVariable to "c"
            )
        }

        val nextStates = StateRuleSelection(dynamicAndStaticKBSolveRequest).behave()

        assertOverFilteredStateInstances<FinalState>(nextStates) { index, finalState ->
            assertOverState<StateEnd>(finalState) {
                it.solve.solution.assertCorrectQueryAndSubstitution(theQuery, correctSubstitutions[index])
            }
        }
    }

}
