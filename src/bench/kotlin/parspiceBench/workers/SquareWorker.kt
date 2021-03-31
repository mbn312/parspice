package parspiceBench.workers

import parspice.sender.DoubleSender
import parspice.worker.OWorker
import parspiceBench.BenchWorker
import kotlin.math.pow

/**
 * The most basic, cheapest case.
 */
class SquareWorker : BenchWorker<Double>(DoubleSender()) {
    override val bytes
        get() = Double.SIZE_BYTES
    override val iterations
        get() = mapOf(
            2 to intArrayOf(1000, 100000),
            4 to intArrayOf(1000, 10000, 100000, 1000000),
            6 to intArrayOf(100, 1000, 100000, 1000000),
            8 to intArrayOf(100, 1000, 100000, 1000000, 10000000, 50000000)
        )
    override val description: String
        get() = "square the input"

    override fun task(i: Int) = i.toDouble().pow(2)
}