package it.unibo.tuprolog.solve.solver.fsm.impl

import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.solver.fsm.State
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils.createSolveRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [AbstractTimedState]
 *
 * @author Enrico
 */
internal class AbstractTimedStateTest {

    private val behaviourResponse = emptySequence<Nothing>()

    /** Creates an [AbstractTimedState] instance with provided parameters, and emptySequence returning behaviour */
    private fun createTimeState(solveRequest: Solve.Request<ExecutionContext>) =
            object : AbstractTimedState(solveRequest) {
                override fun behaveTimed(): Sequence<State> = behaviourResponse
            }

    /** Creates a test solve request with given timing fields, or defaults if not provided */
    private fun createTimedRequest(requestIssuingInstant: TimeInstant? = null, maxDuration: TimeDuration? = null) =
            with(createSolveRequest(Truth.`true`())) {
                copy(
                        requestIssuingInstant = requestIssuingInstant ?: this.requestIssuingInstant,
                        executionMaxDuration = maxDuration ?: this.executionMaxDuration
                )
            }

    @Test
    fun behaveWorksAsUsualIfLongMaxValueTimeoutSpecified() {
        val toBeTested = createTimeState(createTimedRequest(maxDuration = TimeDuration.MAX_VALUE))

        assertEquals(behaviourResponse, toBeTested.behave())
    }

    @Test
    fun behaveChecksIfTimeoutElapsedAndIfItIsYieldsStateEndHalt() {
        val toBeTested = createTimeState(createTimedRequest(maxDuration = 0))

        val state = toBeTested.behave().single()
        assertEquals(state::class, StateEnd.Halt::class)
        assertEquals((state as StateEnd.Halt).exception::class, TimeOutException::class)
    }

    @Test
    fun behaveChecksIfTimeoutElapsedAndIfNotGoesIntoBehaveTimedBehaviour() {
        val toBeTested = createTimeState(createTimedRequest(currentTimeInstant(), 1000))

        assertEquals(behaviourResponse, toBeTested.behave())
    }

    @Test
    fun behaveCanBeCalledMultipleTimesYieldingAlwaysSameResponse() {
        val maxDuration = 1000L
        val currentTime = currentTimeInstant()

        val toBeTested = createTimeState(createTimedRequest(currentTimeInstant(), maxDuration))
        while (currentTime + maxDuration * 1.5 > currentTimeInstant())
            assertEquals(behaviourResponse.toList(), toBeTested.behave().toList())
    }

    @Test
    fun getCurrentTimeReturnsCurrentTime() {
        val toleranceInMillis = 500L

        val currentTime = currentTimeInstant()
        val toBeTested = createTimeState(createTimedRequest()).getCurrentTime()

        assertTrue { currentTime <= toBeTested }
        assertTrue("getCurrentTime() exceeded the $toleranceInMillis tolerance: $currentTime << $toBeTested") {
            toBeTested < currentTime + toleranceInMillis
        }
    }
}
