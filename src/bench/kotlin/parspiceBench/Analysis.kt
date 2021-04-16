package parspiceBench

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression
import java.io.File

/**
 * Main function of the `gradle benchmark` task.
 *
 * Performs MLR on the basic model:
 * T ~ T_0 / w + D
 *
 * This model was found to have a good trade-off of simplicity
 * and accuracy. True accuracy and realism aren't the goal;
 * its meant to be a simple heuristic model for determining whether
 * to use ParSPICE or not.
 */
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
    val beta = regression.estimateRegressionParameters()
    val betaString = regression.estimateRegressionParameters().map {
        String.format("%.2f", it)
    }
    val betaSE = regression.estimateRegressionParametersStandardErrors().map {
        String.format("%.3f", it)
    }

    val breakEven = arrayOf(
        String.format("%.3f", 1/beta[1]),
        String.format("%.3f", beta[0]/beta[1])
    )

    println("""
        Benchmark results, based on ${runs.size} runs.
        You can print these results again with `gradle bench`, without
        re-running the entire benchmark. (The results are cached.)

        To run the benchmark again, run `gradle clean` first.
        
        
        [Model]
        
             ${" ".repeat(betaString[0].length)}T_0
        T = ${betaString[0]} --- + ${betaString[1]}[ms/MB] D
              ${" ".repeat(betaString[0].length)}w
        
        where:  T   = Multithreaded ParSPICE runtime, in ms
                T_0 = Single threaded runtime, in ms
                w   = Number of workers
                D   = Total data to transfer, in MB
        
        [Confidence]
        
        Adjusted R^2:         ${String.format("%.4f", regression.calculateAdjustedRSquared())}
        Regression Std. Err.: ${String.format("%.4f", regression.estimateRegressionStandardError())} ms
        Beta Std. Errs.:      ${betaSE[0]}, ${betaSE[1]}[ms/MB]
        
        [Break-Even Point]
        
        Setting T = T0 gives the estimated maximum data that can be sent
        per iteration before ParSPICE is slower than direct evaluation:
        
        d = (${breakEven[0]} - ${breakEven[1]} / w)[B/ns] t
        
        where:  d = data sent per iteration, in bytes
                w = number of workers
                t = average single-threaded time per iteration, in ns
        """.trimIndent())
}

/*
         T_0
T = 1.37 --- + 3.2 D
          W

T = I(B1 t/w + B2 d)

T - T0 = I(B1 t/w + B2 d) - I t
       = I ( B1 t (1/w - 1) + B2 d)

0 = (B1/w - 1) t + B2 d

-> d = (1/B2) (1-B1/w) t
     = (1/B2 - B1/B2/w) t
 */