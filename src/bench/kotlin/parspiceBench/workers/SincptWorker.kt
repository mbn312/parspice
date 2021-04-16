package parspiceBench.workers

import parspice.sender.DoubleArraySender
import parspiceBench.BenchWorker
import spice.basic.CSPICE.*

/**
 * A more expensive CSPICE task, with several calls including sincpt.
 */
class SincptWorker: BenchWorker<DoubleArray>(DoubleArraySender(3)) {
    override val bytes
        get() = 3 * Double.SIZE_BYTES
    override val iterations
        get() = mapOf(
            2 to intArrayOf(1000, 100000),
            4 to intArrayOf(1000, 10000, 100000),
            6 to intArrayOf(1000, 100000, 1000000),
            8 to intArrayOf(1000, 100000, 1000000, 2000000)
        )
    override val description: String
        get() = "str2et -> getfov -> sincpt (long)"

    var shape = arrayOfNulls<String>(1)
    var insfrm = arrayOfNulls<String>(1)
    var bsight = DoubleArray(3)
    var n = IntArray(1)
    var bounds = DoubleArray(12)
    var point = DoubleArray(3)
    var trgepc = DoubleArray(1)
    var srfvec = DoubleArray(3)
    var found = BooleanArray(1)
    var nacid = 0
    var et = 0.0

    override fun setup() {
        System.loadLibrary("JNISpice");
        furnsh("src/bench/spice/fovint.tm")
        nacid = bodn2c("CASSINI_ISS_NAC")
    }

    override fun task(i: Int): DoubleArray {
        et = str2et("2004 jun 11 19:32:00") - i / 20000.0
        getfov(nacid, shape, insfrm, bsight, n, bounds)
        sincpt(
            "Ellipsoid", "PHOEBE", et, "IAU_PHOEBE",
            "LT+S", "CASSINI", insfrm[0], bounds,
            point, trgepc, srfvec, found
        )
        return point
    }
}