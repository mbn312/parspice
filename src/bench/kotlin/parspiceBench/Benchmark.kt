package parspiceBench

import parspice.ParSPICE
import parspiceBench.workers.*
import java.io.File

val par = ParSPICE("build/libs/bench.jar", 50050)

/**
 * Main function of the `gradle runBenchmark` task.
 *
 * Runs a series of example workers, with varying numbers of
 * worker processes and total tasks.
 *
 * It stores the results in a csv with header:
 * caseDescription, numTasks, numWorkers, messageSize, taskTime, totalTime
 */
fun main() {

    val workers = arrayOf(
        SquareWorker(),
        LargeOutputWorker(),
        GfposcWorker(),
        SincptWorker(),
        MxvhatWorker(),
        MxvhatWorkerJava()
    )

    println("Running ${workers.size} benchmark cases. This can take a few minutes.\n")

    val runs: MutableList<Run> = mutableListOf()

    for (i in workers.indices) {
        println("Running case $i [${workers[i].description}]")
        runs.addAll(run(workers[i]))
    }

    File("benchmark_log.csv").writeText(
        "${runs[0].headerString()}\n${runs.joinToString("\n")}"
    )
}

fun <T> run(worker: BenchWorker<T>): MutableList<Run> {
    val taskTime = taskTime(worker)

    val runs: MutableList<Run> = mutableListOf()
    for ((numWorkers, numTasksList) in worker.numParallelTasks) {
        for (numTasks in numTasksList) {
            tick()
            par.run(worker, numTasks, numWorkers)
            val time = tock()
            runs.add(
                Run (
                    worker.description,
                    numTasks,
                    numWorkers,
                    worker.bytes,
                    taskTime,
                    time
                )
            )
        }
    }
    return runs
}

fun <T> taskTime(worker: BenchWorker<T>): Double {
    tick()
    worker.setup()
    for (i in 0 until worker.numSingleThreadedTasks) {
        worker.task(i)
    }
    return tock().toDouble() / worker.numSingleThreadedTasks
}

var startTime: Long = -1

fun tick() {
    startTime = System.currentTimeMillis()
}
fun tock(): Long {
    return System.currentTimeMillis() - startTime
}