package parspiceBench

/**
 * A data class for storing the performance results of a single run
 *
 * @property caseDescription the string description of the case, used as
 *                           a bodgy identifier
 * @property numTasks the number of tasks of the task
 * @property numWorkers the number of workers used
 * @property messageSize the number of bytes sent per iteration
 * @property taskTime the average time to run the task once on one process
 * @property totalTime the total time it took to run multiprocessed
 */
data class Run(
    val caseDescription: String,
    val numTasks: Int,
    val numWorkers: Int,
    val messageSize: Int,
    val taskTime: Double,
    val totalTime: Long,
    val java: Boolean
) {
    override fun toString(): String {
        return "$caseDescription,$numTasks,$numWorkers,$messageSize,$taskTime,$totalTime,$java"
    }

    fun headerString(): String {
        return "caseDescription,numTasks,numWorkers,messageSize,taskTime,totalTime,java"
    }

    companion object {
        fun fromString(s: String): Run {
            val parts = s.split(',')
            return Run(
                parts[0],
                parts[1].toInt(),
                parts[2].toInt(),
                parts[3].toInt(),
                parts[4].toDouble(),
                parts[5].toLong(),
                parts[6].toBoolean()
            )
        }
    }
}
