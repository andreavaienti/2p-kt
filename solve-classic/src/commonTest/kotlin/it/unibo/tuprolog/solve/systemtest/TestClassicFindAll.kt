package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.ClassicSolverFactory
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFindAll
import kotlin.test.Test

class TestClassicFindAll : TestFindAll, SolverFactory by ClassicSolverFactory {

    private val prototype = TestFindAll.prototype(this)

    @Test
    override fun testFindXInDiffValues() {
        prototype.testFindXInDiffValues()
    }

    @Test
    override fun testFindSumResult() {
        prototype.testFindSumResult()
    }

    @Test
    override fun testFindXinFail() {
        prototype.testFindXinFail()
    }

    @Test
    override fun testFindXinSameXValues() {
        prototype.testFindXinSameXValues()
    }

    @Test
    override fun testResultListIsCorrect() {
        prototype.testResultListIsCorrect()
    }

    @Test
    override fun testFindXtoDoubleAssigment() {
        prototype.testFindXtoDoubleAssigment()
    }

    @Test
    override fun testFindXinGoal() {
        prototype.testFindXinGoal()
    }

    @Test
    override fun testFindXinNumber() {
        prototype.testFindXinNumber()
    }

    @Test
    override fun testFindXinCall() {
        prototype.testFindXinCall()
    }
}
