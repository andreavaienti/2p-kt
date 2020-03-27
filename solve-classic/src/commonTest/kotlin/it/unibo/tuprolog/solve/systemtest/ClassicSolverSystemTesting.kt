package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClassicSolverSystemTesting : SolverFactory {

    /** A short test max duration */
    private val shortDuration = 500L
    /** A medium test max duration */
    private val mediumDuration = 1000L
    /** A long test max duration */
    private val longDuration = 2000L

    private val prototype = SolverTestPrototype(this)

    override val defaultLibraries: Libraries = Libraries(DefaultBuiltins)

    override val defaultBuiltins: AliasedLibrary
        get() = DefaultBuiltins

    override fun solverOf(
        libraries: Libraries,
        flags: PrologFlags,
        staticKB: ClauseDatabase,
        dynamicKB: ClauseDatabase,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ): Solver = Solver.classic(libraries, flags, staticKB, dynamicKB, stdIn, stdOut, stdErr, warnings)

    override fun mutableSolverOf(
        libraries: Libraries,
        flags: PrologFlags,
        staticKB: ClauseDatabase,
        dynamicKB: ClauseDatabase,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ): MutableSolver =
        MutableSolver.classic(libraries, flags, staticKB, dynamicKB, stdIn, stdOut, stdErr, warnings)

    @Test
//    @Ignore
    fun entryPointForManualTests() {
        prolog {
            val solver = mutableSolverOf(
                libraries = defaultLibraries
            )

            val prints = mutableListOf<String>()

            solver.standardOutput?.addListener { prints.add(it) }

            solver.assertZ("f"(1))
            solver.assertZ("f"(2))
            solver.assertZ("f"(3))

            assertTrue {
                solver.dynamicKb.contains("f"("X"))
            }

            val query = "f"("X") and "write"("hello world") and "nl" and "assert"("a") and "a"

            solver.solve(query).forEach(::println)

            assertEquals("hello world", prints[0])
            assertEquals("\n", prints[1])
            assertTrue {
                solver.dynamicKb.contains(atomOf("a"))
            }
            solver.retractAll("f"("X"))
            assertTrue {
                "f"("X") !in solver.dynamicKb
            }
        }
    }

    @Test
    fun testIfThen2() {
        prototype.testIfThen2()
    }

    @Test
    fun testIfThen1() {
        prototype.testIfThen1()
    }

    @Test
    fun testIfThenElse1() {
        prototype.testIfThenElse1()
    }

    @Test
    fun testIfThenElse2() {
        prototype.testIfThenElse1()
    }

    @Test
    fun testTimeout1() {
        prototype.testTimeout1()
    }

    @Test
    fun testTimeout2() {
        prototype.testTimeout2()
    }

    @Test
    fun testTimeout3() {
        prototype.testTimeout3()
    }

    @Test
    fun testTimeout4() {
        prototype.testTimeout4()
    }

    @Test
    fun testTrue() {
        prototype.testTrue()
    }

    @Test
    fun testUnification() {
        prototype.testUnification(mediumDuration)
    }

    @Test
    fun testSimpleCutAlternatives() {
        prototype.testSimpleCutAlternatives(mediumDuration)
    }

    @Test
    fun testCutAndConjunction() {
        prototype.testCutAndConjunction(mediumDuration)
    }

    @Test
    fun testCutConjunctionAndBacktracking() {
        prototype.testCutConjunctionAndBacktracking(mediumDuration)
    }

    @Test
    fun testMaxDurationParameterAndTimeOutException() {
        prototype.testMaxDurationParameterAndTimeOutException(shortDuration)
    }

    @Test
    fun testPrologStandardSearchTreeExample() {
        prototype.testPrologStandardSearchTreeExample(mediumDuration)
    }

    @Test
    fun testPrologStandardSearchTreeWithCutExample() {
        prototype.testPrologStandardSearchTreeWithCutExample(mediumDuration)
    }

    @Test
    fun testBacktrackingWithCustomReverseListImplementation() {
        prototype.testBacktrackingWithCustomReverseListImplementation(mediumDuration)
    }

    @Test
    fun testWithPrologStandardConjunctionExamples() {
        prototype.testWithPrologStandardConjunctionExamples(mediumDuration)
    }

    @Test
    fun testConjunctionProperties() {
        prototype.testConjunctionProperties(mediumDuration)
    }

    @Test
    fun testCallPrimitive() {
        prototype.testCallPrimitive(mediumDuration)
    }

    @Test
    fun testCallPrimitiveTransparency() {
        prototype.testCallPrimitiveTransparency(mediumDuration)
    }

    @Test
    fun testCatchPrimitive() {
        prototype.testCatchPrimitive(mediumDuration)
    }

    @Test
    fun testCatchPrimitiveTransparency() {
        prototype.testCatchPrimitiveTransparency(mediumDuration)
    }

    @Test
    fun testHaltPrimitive() {
        prototype.testHaltPrimitive(mediumDuration)
    }

    @Test
    fun testNotPrimitive() {
        prototype.testNotPrimitive(mediumDuration)
    }

    @Test
    fun testNotModularity() {
        prototype.testNotModularity(mediumDuration)
    }

    @Test
    fun testIfThenRule() {
        prototype.testIfThenRule(mediumDuration)
    }

    @Test
    fun testIfTheElseRule() {
        prototype.testIfThenElseRule(mediumDuration)
    }

    @Test
    fun testNumbersRangeListGeneration() {
        prototype.testNumbersRangeListGeneration(mediumDuration)
    }

    @Test
    fun testConjunction() {
        prototype.testConjunction(mediumDuration)
    }

    @Test
    fun testConjunctionWithUnification() {
        prototype.testConjunctionWithUnification(mediumDuration)
    }

    @Test
    fun testBuiltinApi() {
        prototype.testBuiltinApi()
    }

    @Test
    fun testDisjunction() {
        prototype.testDisjunction(mediumDuration)
    }

    @Test
    fun testFailure() {
        prototype.testFailure(mediumDuration)
    }

    @Test
    fun testDisjunctionWithUnification() {
        prototype.testDisjunctionWithUnification(mediumDuration)
    }

    @Test
    fun testConjunctionOfConjunctions() {
        prototype.testConjunctionOfConjunctions(mediumDuration)
    }

    @Test
    fun testMember() {
        prototype.testMember(mediumDuration)
    }

    @Test
    fun testBasicBacktracking1() {
        prototype.testBasicBacktracking1(mediumDuration)
    }

    @Test
    fun testBasicBacktracking2() {
        prototype.testBasicBacktracking2(mediumDuration)
    }

    @Test
    fun testBasicBacktracking3() {
        prototype.testBasicBacktracking3(mediumDuration)
    }

    @Test
    fun testBasicBacktracking4() {
        prototype.testBasicBacktracking4(mediumDuration)
    }
}