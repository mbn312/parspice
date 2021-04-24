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
                it.numTasks.toDouble() * it.taskTime / it.numWorkers,
                it.numTasks.toDouble() * it.taskTime,
                it.messageSize.toDouble() * it.numTasks / 1000000.0
            )
        }.toTypedArray()
    )
    val beta = regression.estimateRegressionParameters()
    val betaString = regression.estimateRegressionParameters().map {
        String.format("%.3f", it)
    }
    val betaSE = regression.estimateRegressionParametersStandardErrors().map {
        String.format("%.3f", it)
    }

    val breakEven = arrayOf(
        String.format("%.3f", (1-beta[1])/beta[2]),
        String.format("%.3f", beta[0]/beta[2])
    )

    println("""
        Benchmark results, based on ${runs.size} runs.
        You can print these results again with `gradle bench`, without
        re-running the entire benchmark. (The results are cached.)

        To run the benchmark again, run `gradle clean` first.
        
        
        [Model]
             / ${betaString[0]}        \
        T = |  ----- + ${betaString[1]} | T0 + ${betaString[2]}[ms/MB] D
             \   W          /
        
        where:  T   = Multithreaded ParSPICE runtime, in ms
                T_0 = Single threaded runtime, in ms
                w   = Number of workers
                D   = Total data to transfer, in MB
        
        [Confidence]
        
        Adjusted R^2:         ${String.format("%.4f", regression.calculateAdjustedRSquared())}
        Regression Std. Err.: ${String.format("%.4f", regression.estimateRegressionStandardError())} ms
        Predictor Std. Errs.: ${betaSE[0]}, ${betaSE[1]}, ${betaSE[2]}[ms/MB]
        
        [Break-Even Point]
        
        Setting T = T0 gives the estimated maximum data that can be sent
        per task before ParSPICE is slower than direct evaluation:
        
        d = (${breakEven[0]} - ${breakEven[1]} / w)[B/ns] t
        
        where:  d = data sent per task, in bytes
                w = number of workers
                t = average single-threaded time per task, in ns
        """.trimIndent())
}

/*
     / B_1      \
T = |  --- + B_3 | T_0 + B_2[ms/MB] D
     \  W       /
 */