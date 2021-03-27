package parspiceBench

data class Run(
    val numIterations: Int,
    val numWorkers: Int,
    val messageSize: Int,
    val taskTime: Long,
    val totalTime: Long
) {
    override fun toString(): String {
        return "$numIterations, $numWorkers, $messageSize, $taskTime, $totalTime"
    }
}
