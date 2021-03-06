package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestArith
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class TestStreamsArith : TestArith, SolverFactory by StreamsSolverFactory {
    private val prototype = TestArith.prototype(this)

    @Test
    override fun testArithDiff() {
        prototype.testArithDiff()
    }

    @Test
    override fun testArithEq() {
        prototype.testArithEq()
    }

    @Test
    override fun testArithGreaterThan() {
        prototype.testArithGreaterThan()
    }

    @Test
    override fun testArithGreaterThanEq() {
        prototype.testArithGreaterThanEq()
    }

    @Test
    override fun testArithLessThan() {
        prototype.testArithLessThan()
    }

    @Test
    override fun testArithLessThanEq() {
        prototype.testArithLessThanEq()
    }
}
