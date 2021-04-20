package parspice.job;

import com.sun.jdi.request.InvalidRequestStateException;
import parspice.ParSPICE;
import parspice.io.IOManager;
import parspice.sender.Sender;

import java.io.IOException;
import java.util.ArrayList;

public abstract class Job<S, I, O> {

    int numWorkers = -1;
    int numTasks = -1;

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

    final void runCommon(ParSPICE par, ArrayList<IOManager<S,I,O>> ioManagers) throws Exception {
        String workerClass = getClass().getName();
        par.checkClass(workerClass);

        String workerJar = par.getWorkerJar();
        int minPort = par.getMinPort();

        if (ioManagers != null) {
            for (IOManager<S,I,O> manager : ioManagers) {
                manager.start();
            }
        }
        Process[] processes = new Process[numWorkers];
        int task = 0;
        for (int i = 0; i < numWorkers; i++) {
            int taskSubset = taskSubset(numTasks, numWorkers, i);
            String args = "-Dname=parspice_worker_" + i +
                    " -cp " + workerJar +
                    " parspice.Worker" +
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
            for (IOManager<S,I,O> manager : ioManagers) {
                manager.join();
            }
        }
        for (int i = 0; i < numWorkers; i++) {
            processes[i].waitFor();
        }
    }

    ArrayList<O> aggregateOutputs(ArrayList<IOManager<S,I,O>> ioManagers) {
        ArrayList<O> results = ioManagers.get(0).getOutputs();
        if (results == null) {
            return null;
        }
        results.ensureCapacity(numTasks);
        for (IOManager<S, I, O> ioManager : ioManagers.subList(1, ioManagers.size())) {
            results.addAll(ioManager.getOutputs());
        }
        return results;
    }

    static int taskSubset(int numTasks, int numWorkers, int i) {
        return numTasks/numWorkers + ((i < numTasks%numWorkers)?1:0);
    }

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
