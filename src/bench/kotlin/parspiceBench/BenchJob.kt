package parspiceBench

import parspice.sender.Sender
import parspice.job.OJob

/**
 * Simple wrapper class for OWorkers, that provides additional information
 * about how many times it should be run, and how many bytes are sent per iteration.
 *
 * @property bytes Number of bytes sent per iteration.
 * @property numSingleThreadedTasks how many tasks should be run during the
 *                                  single threaded case
 * @property numParallelTasks a map from the number of workers, to a list of task counts
 *                            to run with that many workers.
 */
abstract class BenchJob<O>(sender: Sender<O>): OJob<O>(sender) {
    abstract val bytes: Int
    open val numSingleThreadedTasks
        get() = 1000000
    open val numParallelTasks
        get() = mapOf(
            2 to intArrayOf(1000, 100000),
            4 to intArrayOf(1000, 10000, 100000, 1000000),
            6 to intArrayOf(100, 1000, 100000, 1000000),
            8 to intArrayOf(100, 1000, 100000, 1000000, 10000000)
        )

    abstract val description: String
}