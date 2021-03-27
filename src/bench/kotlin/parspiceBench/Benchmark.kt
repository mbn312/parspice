package parspiceBench

import parspice.ParSPICE
import parspiceBench.workers.SquareInput

val par = ParSPICE("build/libs/bench-1.0-SNAPSHOT.jar", 50050)

fun main() {
    val runs = run(SquareInput())
    println(runs.joinToString("\n"))
}

fun <T> run(worker: BenchWorker<T>): List<Run> {
    val singleThreadTime = singleThreadTime(worker)

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
                    singleThreadTime,
                    stopTime
                )
            )
        }
    }
    return runs
}

fun <T> singleThreadTime(worker: BenchWorker<T>): Long {
    tick()
    for (i in 0 until worker.singleIterations) {
        worker.task(i)
    }
    return tock()
}

var startTime: Long = -1

fun tick() {
    startTime = System.currentTimeMillis()
}
fun tock(): Long {
    return System.currentTimeMillis() - startTime
}