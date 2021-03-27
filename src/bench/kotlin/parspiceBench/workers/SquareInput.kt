package parspiceBench.workers

import parspice.sender.DoubleArraySender
import parspice.sender.Sender
import parspice.worker.OWorker
import parspiceBench.BenchWorker
import kotlin.math.pow

class SquareInput : BenchWorker<DoubleArray>() {
    override val bytes = 2*Double.SIZE_BYTES
    override val iterations: Map<Int, IntArray> = mapOf(
        2 to intArrayOf(1000, 100000),
        4 to intArrayOf(1000, 10000, 100000, 1000000),
        6 to intArrayOf(100, 1000, 100000, 1000000),
        8 to intArrayOf(100, 1000, 100000, 1000000, 10000000, 50000000)
    )

    override fun getOutputSender(): Sender<DoubleArray> {
        return DoubleArraySender(2)
    }

    override fun task(i: Int): DoubleArray {
        return doubleArrayOf(i.toDouble(), i.toDouble().pow(2))
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            OWorker.run(SquareInput(), args)
        }
    }
}