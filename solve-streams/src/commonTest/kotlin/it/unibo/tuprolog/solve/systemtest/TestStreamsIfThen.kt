package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestIfThen
import kotlin.test.Test

class TestStreamsIfThen : TestIfThen, SolverFactory by StreamsSolverFactory  {
    private val prototype = TestIfThen.prototype(this)

    @Test
    override fun testIfThenFail() {
        prototype.testIfThenFail()
    }

    @Test
    override fun testIfThenFailTrue() {
        prototype.testIfThenFailTrue()
    }

    @Test
    override fun testIfThenOrWithDoubleSub() {
        prototype.testIfThenOrWithDoubleSub()
    }

    @Test
    override fun testIfThenTrue() {
        prototype.testIfThenTrue()
    }

    @Test
    override fun testIfThenXOr() {
        prototype.testIfThenXOr()
    }

    @Test
    override fun testIfThenXtoOne() {
        prototype.testIfThenXtoOne()
    }
}