package parspiceBench

import parspice.ParSPICE
import parspiceBench.workers.*
import java.io.File

val par = ParSPICE("build/libs/bench.jar", 50050, System.getenv("JNISPICE_LIB"))

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

    val jobs = arrayOf(
        LargeOutputWorker(),
        SquareWorker(),
        GfposcWorker(),
        SincptWorker(),
        MxvhatWorker(),
        MxvhatWorkerJava()
    )

    println("Running ${jobs.size} benchmark jobs. This can take a few minutes.\n")

    val runs: MutableList<Run> = mutableListOf()

    for (i in jobs.indices) {
        println("Running case $i [${jobs[i].description}]")
        runs.addAll(run(jobs[i]))
    }

    File("benchmark_log.csv").writeText(
        "${runs[0].headerString()}\n${runs.joinToString("\n")}"
    )
}

fun <T> run(job: BenchWorker<T>): MutableList<Run> {
    val taskTime = taskTime(job)

    val runs: MutableList<Run> = mutableListOf()
    for ((numWorkers, numTasksList) in job.numParallelTasks) {
        for (numTasks in numTasksList) {
            tick()
            job.init(numWorkers, numTasks).run(par)
            val time = tock()
            runs.add(
                Run (
                    job.description,
                    numTasks,
                    numWorkers,
                    job.bytes,
                    taskTime,
                    time,
                    job.java
                )
            )
        }
    }
    return runs
}

fun <T> taskTime(job: BenchWorker<T>): Double {
    tick()
    job.setup()
    for (i in 0 until job.numSingleThreadedTasks) {
        job.task(i)
    }
    return tock().toDouble() / job.numSingleThreadedTasks
}

var startTime: Long = -1

/**
 * Start the timer
 */
fun tick() {
    startTime = System.currentTimeMillis()
}

/**
 * End the timer
 * @return timer result in milliseconds
 */
fun tock(): Long {
    return System.currentTimeMillis() - startTime
}