package parspiceBench

/**
 * A data class for storing the performance results of a single run
 *
 * @property numIterations the number of iterations of the task
 * @property numWorkers the number of workers used
 * @property messageSize the number of bytes sent per iteration
 * @property taskTime the average time to run the task once on one process
 * @property totalTime the total time it took to run multiprocessed
 */
data class Run(
    val numIterations: Int,
    val numWorkers: Int,
    val messageSize: Int,
    val taskTime: Double,
    val totalTime: Long
) {
    override fun toString(): String {
        return "$numIterations,$numWorkers,$messageSize,$taskTime,$totalTime"
    }

    fun headerString(): String {
        return "numIterations,numWorkers,messageSize,taskTime,totalTime"
    }

    companion object {
        fun fromString(s: String): Run {
            val parts = s.split(',')
            return Run(
                parts[0].toInt(),
                parts[1].toInt(),
                parts[2].toInt(),
                parts[3].toDouble(),
                parts[4].toLong()
            )
        }
    }
}
