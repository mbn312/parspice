package parspiceBench.workers

import parspice.sender.DoubleSender
import parspice.worker.OWorker
import parspiceBench.BenchWorker
import kotlin.math.pow

class SquareInput : BenchWorker<Double>() {
    override val bytes = Double.SIZE_BYTES
    override val iterations: Map<Int, IntArray> = mapOf(
        2 to intArrayOf(1000, 100000),
        4 to intArrayOf(1000, 10000, 100000, 1000000),
        6 to intArrayOf(100, 1000, 100000, 1000000),
        8 to intArrayOf(100, 1000, 100000, 1000000, 10000000, 50000000)
    )

    override fun getOutputSender() = DoubleSender()

    override fun task(i: Int) = i.toDouble().pow(2)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            OWorker.run(SquareInput(), args)
        }
    }
}