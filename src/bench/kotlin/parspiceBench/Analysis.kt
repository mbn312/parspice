package parspiceBench

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression
import java.io.File

fun main() {
    val csv = File("benchmark_log.csv").readLines()
    val runs = csv.subList(1, csv.size).map {
        Run.fromString(it)
    }
    val regression = OLSMultipleLinearRegression()
    regression.isNoIntercept = true
    regression.newSampleData(
        runs.map {
            it.totalTime.toDouble()
        }.toDoubleArray(),
        runs.map {
            doubleArrayOf(
                it.numIterations.toDouble() * it.taskTime / it.numWorkers,
                it.messageSize.toDouble() * it.numIterations / 1000000.0
            )
        }.toTypedArray()
    )
    val beta = regression.estimateRegressionParameters().map {
        String.format("%.2f", it)
    }
    val betaSE = regression.estimateRegressionParametersStandardErrors().map {
        String.format("%.3f", it)
    }

    println("""
        Benchmark results, based on ${runs.size} runs.
        You can print these results again with `gradle bench`, without
        re-running the entire benchmark. (The results are cached.)

        To run the benchmark again, run `gradle clean` first.
        
        
        [Heuristic]
        
             ${" ".repeat(beta[0].length)}T_0
        T = ${beta[0]} --- + ${beta[1]}[ms/MB] D
              ${" ".repeat(beta[0].length)}W
        
        where:  T   = Multithreaded ParSPICE runtime, in ms
                T_0 = Single threaded runtime, in ms
                W   = Number of workers
                D   = Total data to transfer, in MB
        
        [Confidence]
        
        Adjusted R^2:         ${String.format("%.4f", regression.calculateAdjustedRSquared())}
        Regression Std. Err.: ${String.format("%.4f", regression.estimateRegressionStandardError())} ms
        Beta Std. Errs.:      ${betaSE[0]}, ${betaSE[1]}[ms/MB]
        """.trimIndent())
}

/*
         T_0
T = 1.37 --- + 3.2 D
          W
 */