package parspiceBench.workers

import parspice.sender.DoubleArraySender
import parspice.sender.IntSender
import parspiceBench.BenchWorker
import spice.basic.CSPICE.*

/**
 * A funky boi that runs gfposc on a time window.
 *
 * Basis for the code comes from the event_finding lesson.
 * Parallelization is achieved by splitting the window up into
 * equal sections, one for each worker.
 *
 * The regression model assumes that each call to task() performs the
 * same amount of computation, but by default this isn't true here.
 * To get around that, the step size decreases proportionally to the number
 * of workers. This means the results of each run won't be *exactly* the same,
 * but that's ok because we aren't checking for correctness here.
 */
class GfposcWorker: BenchWorker<DoubleArray>(DoubleArraySender()) {

    override val bytes: Int
        // this is an approximation (the size varies with the number of workers
        get() = Int.SIZE_BYTES + 3*Double.SIZE_BYTES
    override val iterations
        get() = mapOf(
            2 to intArrayOf(2),
            3 to intArrayOf(3),
            5 to intArrayOf(5),
            8 to intArrayOf(8)
        )
    override val singleIterations: Int
        get() = 1
    override val description: String
        get() = "gfposc with small step (long)"

    override fun setup() {
        System.loadLibrary("JNISpice")
        furnsh("src/bench/spice/viewpr.tm")
    }

    override fun task(i: Int): DoubleArray {
        val etbegFull = str2et("2004 MAY 2 TDB")
        val etendFull = str2et("2004 MAY 6 TDB")

        val width = (etendFull - etbegFull) / getNumWorkers()
        val etbeg = etbegFull + getWorkerID() * width
        val etend = etbeg + width

        val cnfine = wninsd(etbeg, etend, DoubleArray(0))

        val riswin = gfposc(
            "MEX", "DSS-14_TOPO", "CN+S", "DSS-14",
            "LATITUDINAL", "LATITUDE", ">", 6.0*rpd(),
            0.0, 1.0 / getNumWorkers(), 1000, cnfine
        )
        return riswin
    }
}