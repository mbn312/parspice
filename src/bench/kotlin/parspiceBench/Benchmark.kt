package parspiceBench

import parspice.ParSPICE
import parspiceBench.workers.MxvhatWorker
import parspiceBench.workers.MxvhatWorkerJava
import parspiceBench.workers.SquareInput
import java.io.File

val par = ParSPICE("build/libs/bench-1.0-SNAPSHOT.jar", 50050)

fun main() {
    System.loadLibrary("JNISpice")

    val runs = run(SquareInput())
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
    for (i in 0 until worker.singleIterations) {
        worker.task(i)
    }
    return tock() / worker.singleIterations.toDouble()
}

var startTime: Long = -1

fun tick() {
    startTime = System.currentTimeMillis()
}
fun tock(): Long {
    return System.currentTimeMillis() - startTime
}