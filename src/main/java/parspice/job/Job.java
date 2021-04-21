package parspice.job;

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
public abstract class Job<O> {

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
        Job<?> job = (Job<?>) Class.forName(args[0]).getConstructor().newInstance();
        try {
            job.inputPort = Integer.parseInt(args[1]);
            job.outputPort = job.inputPort + 1;
            job.startIndex = Integer.parseInt(args[2]);
            job.taskSubset = Integer.parseInt(args[3]);
            job.workerID = Integer.parseInt(args[4]);
            job.numWorkers = Integer.parseInt(args[5]);
            job.numTasks = Integer.parseInt(args[6]);

            job.startConnections();
            job.setupWrapper();
            job.taskWrapper();
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();

            FileWriter writer = new FileWriter("ParSPICE_worker_log_" + job.workerID + ".txt");

            writer.write("workerName\t" + args[0]);
            writer.write("\ninputPort\t" + job.inputPort);
            writer.write("\noutputPort\t" + job.outputPort);
            writer.write("\nstartIndex\t" + job.startIndex);
            writer.write("\ntaskSubset\t" + job.taskSubset);
            writer.write("\nworkerID\t" + job.workerID);
            writer.write("\nnumWorkers\t" + job.numWorkers);
            writer.write("\nnumTasks\t" + job.numTasks + "\n\n");

            writer.write(e.toString());
            writer.write("\n\n");
            PrintWriter printer = new PrintWriter(writer);
            e.printStackTrace(printer);
            printer.close();
            writer.flush();
            writer.close();
        } finally {
            job.endConnections();
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

    /**
     * [main] Common logic needed by all Job.run functions.
     *
     * Starts the IOManagers (if they exist), starts the worker processes, then waits for all
     * processes and threads to join.
     *
     * This function is intentionally package-private, so that user extensions of Job
     * cannot call this function.
     *
     * @param par a ParSPICE instance for starting the worker processes.
     * @param ioManagers the IOManagers for all jobs except AutoJobs (null if AutoJob).
     * @throws Exception
     */
    final void runCommon(ParSPICE par, ArrayList<IOManager<?,?,O>> ioManagers) throws Exception {
        String workerClass = getClass().getName();
        par.checkClass(workerClass);

        String workerJar = par.getWorkerJar();
        int minPort = par.getMinPort();

        if (ioManagers != null) {
            for (IOManager<?,?,O> manager : ioManagers) {
                manager.start();
            }
        }
        Process[] processes = new Process[numWorkers];
        int task = 0;
        for (int i = 0; i < numWorkers; i++) {
            int taskSubset = taskSubset(numTasks, numWorkers, i);
            String args = "-Dname=parspice_worker_" + i +
                    " -cp " + workerJar +
                    " parspice.job.Job" +
                    " " + workerClass +
                    " " + (minPort + 2*i) +
                    " " + task +
                    " " + taskSubset +
                    " " + i +
                    " " + numWorkers +
                    " " + numTasks;
            processes[i] = Runtime.getRuntime().exec("java " + args);
            task += taskSubset;
        }
        if (ioManagers != null) {
            for (IOManager<?,?,O> manager : ioManagers) {
                manager.join();
            }
        }
        for (int i = 0; i < numWorkers; i++) {
            processes[i].waitFor();
        }
    }

    /**
     * [main] Collects the outputs from the OServers inside the given list of IOManagers.
     *
     * This function is intentionally package-private, so that user extensions of Job
     * cannot call this function.
     *
     * @param ioManagers the list of ioManagers to collect the outputs from.
     * @return The arraylist of outputs.
     */
    ArrayList<O> collectOutputs(ArrayList<IOManager<?,?,O>> ioManagers) {
        ArrayList<O> results = ioManagers.get(0).getOutputs();
        if (results == null) {
            return null;
        }
        results.ensureCapacity(numTasks);
        for (IOManager<?, ?, O> ioManager : ioManagers.subList(1, ioManagers.size())) {
            results.addAll(ioManager.getOutputs());
        }
        return results;
    }

    /**
     * [main] Calculate how many tasks should be given to a particular worker.
     *
     * Each worker is given an almost-equal taskSubset. If numTasks is not
     * an even multiple of numWorkers, the remainder is spread across the
     * first numTasks % numWorkers workers.
     *
     * This function is intentionally package-private, so that user extensions of Job
     * cannot call this function.
     *
     * @param numTasks total number of tasks
     * @param numWorkers number of workers
     * @param i the index of a particular worker
     * @return the number of tasks that worker should run
     */
    static int taskSubset(int numTasks, int numWorkers, int i) {
        return numTasks/numWorkers + ((i < numTasks%numWorkers)?1:0);
    }

    /**
     * [main] Checks that the specified numbers of workers and tasks are valid.
     * Called at the end of every `init(...)` function.
     *
     * This function is intentionally package-private, so that user extensions of Job
     * cannot call this function.
     *
     * @throws IllegalStateException if either numWorkers or numTasks is unspecified or less than 1.
     */
    void validate() throws IllegalStateException {
        if (numWorkers == -1) {
            throw new IllegalStateException("Number of workers must be specified");
        } else if (numWorkers < 1) {
            throw new IllegalStateException("Number of workers cannot be less than 1, was " + numWorkers);
        }

        if (numTasks == -1) {
            throw new IllegalStateException("Number of tasks must be specified");
        } else if (numTasks < 1) {
            throw new IllegalStateException("Number of tasks cannot be less than 1, was " + numWorkers);
        }
    }
}
