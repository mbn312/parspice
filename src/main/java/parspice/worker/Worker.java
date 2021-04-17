package parspice.worker;

import java.io.FileWriter;
import java.io.PrintWriter;

public abstract class Worker {

    /**
     * Unique ID for the worker, in the range [0, numWorkers)
     */
    static int workerID = 0;

    /**
     * Total number of workers in this job.
     */
    static int numWorkers = 1;

    /**
     * Total number of iterations to be run.
     */
    static int numTasks = 1;

    /**
     * Port used to receive inputs.
     */
    static int inputPort = 0;

    /**
     * Port used to send outputs.
     */
    static int outputPort = 1;

    /**
     * Iteration index that this worker starts at.
     */
    static int startIndex = 0;

    /**
     * How many tasks this worker needs to run.
     */
    static int taskSubset = 1;

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
        Worker worker = (Worker) Class.forName(args[0]).getConstructor().newInstance();
        try {
            inputPort = Integer.parseInt(args[1]);
            outputPort = inputPort + 1;
            startIndex = Integer.parseInt(args[2]);
            taskSubset = Integer.parseInt(args[3]);
            workerID = Integer.parseInt(args[4]);
            numWorkers = Integer.parseInt(args[5]);
            numTasks = Integer.parseInt(args[6]);

            worker.startConnections();
            worker.setup();
            worker.run();
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();

            FileWriter writer = new FileWriter("ParSPICE_worker_log_" + workerID + ".txt");

            writer.write("workerName\t" + args[0]);
            writer.write("\ninputPort\t" + inputPort);
            writer.write("\noutputPort\t" + (inputPort + 1));
            writer.write("\nstartIndex\t" + startIndex);
            writer.write("\ntaskSubset\t" + taskSubset);
            writer.write("\nworkerID\t" + workerID);
            writer.write("\nnumWorkers\t" + numWorkers);
            writer.write("\nnumTasks\t" + numTasks + "\n\n");

            writer.write(e.toString());
            writer.write("\n\n");
            PrintWriter printer = new PrintWriter(writer);
            e.printStackTrace(printer);
            printer.close();
            writer.flush();
            writer.close();
        } finally {
            worker.endConnections();
        }
    }

    /**
     * Called only once, before repeatedly calling {@code task(i)}.
     *
     * If you need to load a native library or perform any one-time preparation,
     * it should be done in this function. If not, you don't need to override it.
     *
     * All setup that might throw an error should be done here, not in the main
     * entry point of the worker; the call to setup is wrapped in a try/catch for error reporting.
     */
    public void setup() throws Exception {}

    /**
     * Contains the task-loop logic specific to each worker type.
     *
     * This function is final in the Worker subclasses, so the user
     * cannot override it.
     *
     * @throws Exception
     */
    public abstract void run() throws Exception;

    public abstract void startConnections() throws Exception;
    public abstract void endConnections() throws Exception;

    public static int getWorkerID() {
        return workerID;
    }

    public static int getNumWorkers() {
        return numWorkers;
    }

    public static int getInputPort() {
        return inputPort;
    }

    public static int getOutputPort() {
        return outputPort;
    }

    public static int getStartIndex() {
        return startIndex;
    }

    public static int getTaskSubset() {
        return taskSubset;
    }

    public static int getNumTasks() {
        return numTasks;
    }
}
