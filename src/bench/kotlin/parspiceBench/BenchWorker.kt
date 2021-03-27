package parspiceBench

import parspice.worker.OWorker

abstract class BenchWorker<T>: OWorker<T> {
    abstract val bytes: Int
    open val singleIterations: Int = 1000000
    open val iterations: Map<Int, IntArray> = mapOf(
        2 to intArrayOf(1000, 100000),
        4 to intArrayOf(1000, 10000, 100000, 1000000),
        6 to intArrayOf(100, 1000, 100000, 1000000),
        8 to intArrayOf(100, 1000, 100000, 1000000, 10000000)
    )
}