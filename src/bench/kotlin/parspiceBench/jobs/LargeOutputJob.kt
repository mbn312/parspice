package parspiceBench.jobs

import parspice.sender.IntArraySender
import parspiceBench.BenchJob

const val LENGTH = 1000

/**
 * This case is to get high-leverage observations on the data size,
 * by running a cheap task that returns many integers.
 */
class LargeOutputJob: BenchJob<IntArray>(IntArraySender(LENGTH)) {
    override val bytes: Int
        get() = LENGTH*Int.SIZE_BYTES
    override val numParallelTasks
        get() = mapOf(
        2 to intArrayOf(1000, 100000),
        4 to intArrayOf(1000, 10000, 100000, 1000000),
        6 to intArrayOf(100, 1000, 100000, 1000000),
        8 to intArrayOf(100, 1000, 100000, 1000000)
    )

    override val description: String
        get() = "output $LENGTH integers"

    override fun task(i: Int): IntArray {
        val result = IntArray(LENGTH)
        for (j in 0 until LENGTH) {
            result[j] = i + j
        }
        return result
    }
}