package parspice.worker;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * The superclass of all Workers.
 *
 * The user is technically able to call the `main(String[] args)` function. Realistically,
 * there is no way to prevent them from doing so. Don't.
 */
public abstract class Worker {

    /**
     * Unique ID for the worker, in the range [0, numWorkers)
     */
    private int workerID = 0;

    /**
     * Total number of workers in this job.
     */
    private int numWorkers = 1;

    /**
     * Total number of iterations to be run.
     */
    private int numTasks = 1;

    /**
     * Port used to receive inputs.
     */
    private int inputPort = 0;

    /**
     * Port used to send outputs.
     */
    private int outputPort = 1;

    /**
     * Iteration index that this worker starts at.
     */
    private int startIndex = 0;

    /**
     * How many tasks this worker needs to run.
     */
    private int taskSubset = 1;

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
     * Gets an instance of the user's Worker and runs it.
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
        Worker worker = null;
        try {
            worker = (Worker) Class.forName(args[0]).getConstructor().newInstance();

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
            System.exit(0);
        }
    }

    /**
     * Contains the setup logic specific to each worker type.
     *
     * This function is final in the Worker subclasses, so the user cannot
     * override it.
     * This function is intentionally package-private, so that user extensions of Worker
     * cannot call this function.
     * @throws Exception any exception the user code needs to throw
     */
    abstract void setupWrapper() throws Exception;

    /**
     * Contains the task-loop logic specific to each worker type.
     *
     * This function is final in the Worker subclasses, so the user
     * cannot override it.
     * This function is intentionally package-private, so that user extensions of Worker
     * cannot call this function.
     *
     * @throws Exception any exception the user code needs to throw
     */
    abstract void taskWrapper() throws Exception;

    /**
     * Start any input/output connections needed for the worker.
     *
     * This function is final in the Worker subclasses, so the user
     * cannot override it.
     * This function is intentionally package-private, so that user extensions of Worker
     * cannot call this function.
     *
     * @throws IOException if the connections cannot be started
     */
    abstract void startConnections() throws IOException;

    /**
     * End any input/output connections needed by the worker.
     *
     * This function is final in the Worker subclasses, so the user
     * cannot override it.
     * This function is intentionally package-private, so that user extensions of Worker
     * cannot call this function.
     *
     * @throws IOException if the connections cannot be ended.
     */
    abstract void endConnections() throws IOException;
}
