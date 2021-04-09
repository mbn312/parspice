package parspiceBench.workers

import parspice.sender.DoubleArraySender
import parspiceBench.BenchWorker
import spice.basic.CSPICE.*

/**
 * A simple CSPICE task.
 */
class MxvhatWorker : BenchWorker<DoubleArray>(DoubleArraySender(3)) {

    override val bytes: Int
        get() = 3*Double.SIZE_BYTES

    override val description: String
        get() = "vhat(mxm(matrix, vector))"

    override fun setup() = System.loadLibrary("JNISpice")

    override fun task(i: Int) = vhat(mxv(mat, doubleArrayOf(1.0, 2.0, i.toDouble())))

    companion object {
        val mat: Array<DoubleArray> = arrayOf(
            doubleArrayOf(1.0, 2.0, 3.0),
            doubleArrayOf(10.0, -2.0, 0.0),
            doubleArrayOf(1.3, 1.0, 0.5),
        )
    }
}