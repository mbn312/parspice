package parspice.worker;

import parspice.ParSPICE;
import parspice.io.IOManager;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.IOException;

/**
 * The superclass of all Jobs.
 *
 * Some functions here are meant to be run on the Main process (marked as [main] in docs), and some
 * are meant to run on the Worker processes (marked as [worker] in docs). Its a little ugly,
 * but the alternative is separating the subclasses' code into another 8 highly entangled
 * files, which would be poor encapsulation.
 *
 * The user is technically able to call the `main(String[] args)` function. Realistically,
 * there is no way to prevent them from doing so. Don't.
 *
 * @param <O> The type of output returned from the job. If the job does not produce output,
 *            Job will be extended as `extends Job<Void>`
 */
public abstract class Worker<O> {

    /**
     * [worker] Unique ID for the worker, in the range [0, numWorkers)
     */
    int workerID = 0;

    /**
     * [main and worker] Total number of workers in this job.
     */
    int numWorkers = 1;

    /**
     * [main and worker] Total number of iterations to be run.
     */
    int numTasks = 1;

    /**
     * [worker] Port used to receive inputs.
     */
    int inputPort = 0;

    /**
     * [worker] Port used to send outputs.
     */
    int outputPort = 1;

    /**
     * [worker] Iteration index that this worker starts at.
     */
    int startIndex = 0;

    /**
     * [worker] How many tasks this worker needs to run.
     */
    int taskSubset = 1;

    public int getWorkerID() {
        return workerID;
    }

    public int getNumWorkers() {
        return numWorkers;
    }

    public int getInputPort() {
        return inputPort;
    }

    public int getNumTasks() {
        return numTasks;
    }

    public int getOutputPort() {
        return outputPort;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getTaskSubset() {
        return taskSubset;
    }

    /**
     * [worker] Gets an instance of the user's Job and runs it.
     *
     * @param args Command line args:
     *             0. Full classname of user's Worker (including package)
     *             1. Input port to use
     *             2. Task index to start at
     *             3. Number of tasks to run
     *             4. Unique ID for this worker
     *             5. Total number of workers
     *             6. Total number of tasks
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Worker<?> worker = null;
        try {
            worker = (Worker<?>) Class.forName(args[0]).getConstructor().newInstance();

            worker.inputPort = Integer.parseInt(args[1]);
            worker.outputPort = worker.inputPort + 1;
            worker.startIndex = Integer.parseInt(args[2]);
            worker.taskSubset = Integer.parseInt(args[3]);
            worker.workerID = Integer.parseInt(args[4]);
            worker.numWorkers = Integer.parseInt(args[5]);
            worker.numTasks = Integer.parseInt(args[6]);

            worker.startConnections();
            worker.setupWrapper();
            worker.taskWrapper();
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();

            FileWriter writer = new FileWriter("ParSPICE_worker_log_" + args[4] + ".txt");

            writer.write("workerName\t" + args[0]);
            writer.write("\ninputPort\t" + args[1]);
            writer.write("\noutputPort\t" + (Integer.parseInt(args[1]) + 1));
            writer.write("\nstartIndex\t" + args[2]);
            writer.write("\ntaskSubset\t" + args[3]);
            writer.write("\nworkerID\t" + args[4]);
            writer.write("\nnumWorkers\t" + args[5]);
            writer.write("\nnumTasks\t" + args[6] + "\n\n");

            writer.write(e.toString());
            writer.write("\n\n");
            PrintWriter printer = new PrintWriter(writer);
            e.printStackTrace(printer);
            printer.close();
            writer.flush();
            writer.close();
        } finally {
            if (worker != null)
                worker.endConnections();
        }
    }

    /**
     * [worker] Contains the setup logic specific to each worker type.
     *
     * This function is final in the Job subclasses, so the user cannot
     * override it.
     * This function is intentionally package-private, so that user extensions of Job
     * cannot call this function.
     */
    abstract void setupWrapper() throws Exception;

    /**
     * [worker] Contains the task-loop logic specific to each worker type.
     *
     * This function is final in the Job subclasses, so the user
     * cannot override it.
     * This function is intentionally package-private, so that user extensions of Job
     * cannot call this function.
     *
     * @throws Exception
     */
    abstract void taskWrapper() throws Exception;

    /**
     * [worker] Start any input/output connections needed for the job.
     *
     * This function is final in the Job subclasses, so the user
     * cannot override it.
     * This function is intentionally package-private, so that user extensions of Job
     * cannot call this function.
     *
     * @throws IOException if the connections cannot be started
     */
    abstract void startConnections() throws IOException;

    /**
     * [worker] End any input/output connections needed by the job.
     *
     * This function is final in the Job subclasses, so the user
     * cannot override it.
     * This function is intentionally package-private, so that user extensions of Job
     * cannot call this function.
     *
     * @throws IOException if the connections cannot be ended.
     */
    abstract void endConnections() throws IOException;
}
