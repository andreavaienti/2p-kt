package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestCall
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsCall : TestCall, SolverFactory by StreamsSolverFactory {
    private val prototype = TestCall.prototype(this)

    @Test
    override fun testCallCut() {
        prototype.testCallCut()
    }

    @Test
    override fun testCallFail() {
        prototype.testCallFail()
    }

    @Test
    override fun testCallFailX() {
        prototype.testCallFailX()
    }

    @Test
    override fun testCallFailCall() {
        prototype.testCallFailCall()
    }

    @Test
    @Ignore
    override fun testCallWriteX() {
        prototype.testCallWriteX()
    }

    @Test
    @Ignore
    override fun testCallWriteCall() {
        prototype.testCallWriteCall()
    }

    @Test
    @Ignore
    override fun testCallX() {
        prototype.testCallX()
    }

    @Test
    @Ignore
    override fun testCallOne() {
        prototype.testCallOne()
    }

    @Test
    @Ignore
    override fun testCallFailOne() {
        prototype.testCallFailOne()
    }

    @Test
    @Ignore
    override fun testCallWriteOne() {
        prototype.testCallWriteOne()
    }

    @Test
    @Ignore
    override fun testCallTrue() {
        prototype.testCallTrue()
    }
}
