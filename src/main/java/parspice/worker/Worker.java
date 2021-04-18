package parspice.worker;

import parspice.Job;
import parspice.sender.Sender;

import java.io.FileWriter;
import java.io.PrintWriter;

public abstract class Worker<S, I, O> {

    /**
     * Unique ID for the worker, in the range [0, numWorkers)
     */
    int workerID = 0;

    /**
     * Total number of workers in this job.
     */
    int numWorkers = 1;

    /**
     * Total number of iterations to be run.
     */
    int numTasks = 1;

    /**
     * Port used to receive inputs.
     */
    int inputPort = 0;

    /**
     * Port used to send outputs.
     */
    int outputPort = 1;

    /**
     * Iteration index that this worker starts at.
     */
    int startIndex = 0;

    /**
     * How many tasks this worker needs to run.
     */
    int taskSubset = 1;

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

            FileWriter writer = new FileWriter("ParSPICE_worker_log_" + worker.workerID + ".txt");

            writer.write("workerName\t" + args[0]);
            writer.write("\ninputPort\t" + worker.inputPort);
            writer.write("\noutputPort\t" + (worker.inputPort + 1));
            writer.write("\nstartIndex\t" + worker.startIndex);
            writer.write("\ntaskSubset\t" + worker.taskSubset);
            writer.write("\nworkerID\t" + worker.workerID);
            writer.write("\nnumWorkers\t" + worker.numWorkers);
            writer.write("\nnumTasks\t" + worker.numTasks + "\n\n");

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
    public abstract void setupWrapper() throws Exception;

    /**
     * Contains the task-loop logic specific to each worker type.
     *
     * This function is final in the Worker subclasses, so the user
     * cannot override it.
     *
     * @throws Exception
     */
    public abstract void taskWrapper() throws Exception;

    public abstract void startConnections() throws Exception;
    public abstract void endConnections() throws Exception;
    public abstract Job<S,I,O> job();

    public abstract Sender<S> getSetupInputSender();
    public abstract Sender<I> getInputSender();
    public abstract Sender<O> getOutputSender();

    public int getWorkerID() {
        return workerID;
    }

    public int getNumWorkers() {
        return numWorkers;
    }

    public int getInputPort() {
        return inputPort;
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

    public int getNumTasks() {
        return numTasks;
    }
}
