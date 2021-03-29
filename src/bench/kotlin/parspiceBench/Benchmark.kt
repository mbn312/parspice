package parspiceBench

import parspice.ParSPICE
import parspiceBench.workers.*
import java.io.File

val par = ParSPICE("build/libs/bench-1.0-SNAPSHOT.jar", 50050)

/**
 * Main function of the `gradle runBenchmark` task.
 *
 * Runs a series of example workers, with varying numbers of
 * worker processes and total iterations.
 *
 * It stores the results in a csv with header:
 * numIterations, numWorkers, messageSize, taskTime, totalTime
 */
fun main() {
    println("Running benchmark. This may take several minutes.")

    val runs = run(LargeOutputWorker())
    runs.addAll(run(SquareWorker()))
    runs.addAll(run(SincptWorker()))
    runs.addAll(run(MxvhatWorker()))
    runs.addAll(run(MxvhatWorkerJava()))
    File("benchmark_log.csv").writeText(
        "${runs[0].headerString()}\n${runs.joinToString("\n")}"
    )
}

fun <T> run(worker: BenchWorker<T>): MutableList<Run> {
    val taskTime = taskTime(worker)

    val runs: MutableList<Run> = mutableListOf()
    for ((numWorkers, iterationsList) in worker.iterations) {
        for (numIterations in iterationsList) {
            tick()
            par.run(worker, numIterations, numWorkers)
            val stopTime = tock()
            runs.add(
                Run (
                    numIterations,
                    numWorkers,
                    worker.bytes,
                    taskTime,
                    stopTime
                )
            )
        }
    }
    return runs
}

fun <T> taskTime(worker: BenchWorker<T>): Double {
    tick()
    worker.setup()
    for (i in 0 until worker.singleIterations) {
        worker.task(i)
    }
    return tock().toDouble() / worker.singleIterations
}

var startTime: Long = -1

fun tick() {
    startTime = System.currentTimeMillis()
}
fun tock(): Long {
    return System.currentTimeMillis() - startTime
}