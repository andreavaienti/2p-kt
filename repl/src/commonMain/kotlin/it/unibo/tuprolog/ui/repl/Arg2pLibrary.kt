package it.unibo.tuprolog.ui.repl

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.classicWithDefaultBuiltins
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.LibraryGroup
import it.unibo.tuprolog.theory.Theory

object Arg2pLibrary {

    fun getSolver(theory: Theory = Theory.empty()): Solver =
        Solver.classicWithDefaultBuiltins(staticKb = theory, libraries = loadLibraries())

    fun testTheory(): Theory =
        loadResourceAsTheory("test-doctor-burden.pl")

    private fun loadLibraries(): Libraries =
        Libraries(libs()
            .map { Library.of(theory = loadResourceAsTheory(it), alias = it) })

    private fun libs() : List<String> = listOf(
        "utils.pl",
        "debug.pl",
        "ruleTranslator.pl",
        "argumentationGraph.pl",
        "argumentLabelling.pl",
        "argumentBPLabelling.pl",
        "statementLabelling.pl",
        "argumentationEngineInterface.pl")
}