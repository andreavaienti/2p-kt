package it.unibo.tuprolog.solve.library.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.library.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.solve.library.stdlib.function.testutils.FunctionUtils.computeOf
import it.unibo.tuprolog.solve.Signature
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [BitwiseRightShift]
 *
 * @author Enrico
 */
internal class BitwiseRightShiftTest {

    @Test
    fun functorNameCorrect() {
        assertEquals(Signature(">>", 2), BitwiseRightShift.signature)
    }

    @Test
    fun computationCorrect() {
        assertEquals(
            Integer.of(4),
            BitwiseRightShift.computeOf(
                Integer.of(16),
                Integer.of(2)
            )
        )
    }

    @Test
    fun rejectedInputs() {
        FunctionUtils.assertRejectsNonIntegerParameters(BitwiseRightShift)
    }

}