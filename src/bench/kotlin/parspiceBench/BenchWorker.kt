package parspiceBench

import parspice.sender.Sender
import parspice.worker.OWorker

/**
 * Simple wrapper class for OWorkers, that provides additional information
 * about how many times it should be run, and how many bytes are sent per iteration.
 *
 * @property bytes Number of bytes sent per iteration.
 * @property singleIterations how many iterations should be run during the
 *                            single threaded case
 * @property iterations a map from the number of workers, to a list of iteration counts
 *                      to run with that many workers.
 */
abstract class BenchWorker<O>(sender: Sender<O>): OWorker<O>(sender) {
    abstract val bytes: Int
    open val singleIterations
        get() = 1000000
    open val iterations
        get() = mapOf(
            2 to intArrayOf(1000, 100000),
            4 to intArrayOf(1000, 10000, 100000, 1000000),
            6 to intArrayOf(100, 1000, 100000, 1000000),
            8 to intArrayOf(100, 1000, 100000, 1000000, 10000000)
        )
}