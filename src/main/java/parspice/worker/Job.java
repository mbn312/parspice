package parspice.worker;

import parspice.ParSPICE;
import parspice.io.IOManager;
import parspice.io.IServer;
import parspice.io.OServer;
import parspice.sender.Sender;

import java.util.ArrayList;
import java.util.List;

public class Job<S,I,O> {
    int numWorkers;
    int numTasks;
    /**
     * [main] setup inputs supplied by the user
     */
    List<S> setupInputs;
    /**
     * [main] inputs supplied by the user
     */
    List<I> inputs;

    Sender<S> setupSender;
    Sender<I> inputSender;
    Sender<O> outputSender;

    private Worker<O> worker;
    protected ArrayList<IOManager<S, I, O>> ioManagers;

    Job(Worker<O> worker) {
        this.worker = worker;
    }

    protected final void runCommon(ParSPICE par) throws Exception {
        boolean hasIO = setupSender != null || inputSender != null || outputSender != null;

        String workerClass = worker.getClass().getName();
        par.checkClass(workerClass);

        String workerJar = par.getWorkerJar();

        Process[] processes = new Process[numWorkers];
        if (hasIO)
            ioManagers = new ArrayList<>(numWorkers);

        int task = 0;
        int minPort = par.getMinPort();

        for (int i = 0; i < numWorkers; i++) {
            int taskSubset = taskSubset(numTasks, numWorkers, i);

            if (hasIO) {
                IServer<S,I> iServer = null;
                OServer<O> oServer = null;
                if (setupSender != null && inputSender != null) {
                    List<I> inputsSublist = inputs.subList(task, task + taskSubset);
                    iServer = new IServer<>(inputSender, setupSender, inputsSublist, setupInputs.get(i), minPort + 2 * i, i);
                } else if (setupSender != null) {
                    iServer = new IServer<>(null, setupSender, null, setupInputs.get(i), minPort + 2 * i, i);
                } else if (inputSender != null) {
                    List<I> inputsSublist = inputs.subList(task, task + taskSubset);
                    iServer = new IServer<>(inputSender, null, inputsSublist, null, minPort + 2 * i, i);
                }
                if (outputSender != null) {
                    oServer = new OServer<>(outputSender, taskSubset, minPort + 2 * i + 1, i);
                }
                IOManager<S,I,O> ioManager = new IOManager<>(iServer, oServer, i);
                ioManagers.add(ioManager);
                ioManager.start();
            }

            String args = "-Dname=parspice_worker_" + i +
                    " -cp " + workerJar +
                    " parspice.worker.Worker" +
                    " " + workerClass +
                    " " + (minPort + 2 * i) +
                    " " + task +
                    " " + taskSubset +
                    " " + i +
                    " " + numWorkers +
                    " " + numTasks;
            processes[i] = Runtime.getRuntime().exec("java " + args);
            task += taskSubset;
        }
        for (int i = 0; i < numWorkers; i++) {
            processes[i].waitFor();
        }
        if (hasIO) {
            for (IOManager<S, I, O> manager : ioManagers) {
                manager.join();
            }
        }
    }

    /**
     * [main] Calculate how many tasks should be given to a particular worker.
     * <p>
     * Each worker is given an almost-equal taskSubset. If numTasks is not
     * an even multiple of numWorkers, the remainder is spread across the
     * first numTasks % numWorkers workers.
     * <p>
     * This function is intentionally package-private, so that user extensions of Job
     * cannot call this function.
     *
     * @param numTasks   total number of tasks
     * @param numWorkers number of workers
     * @param i          the index of a particular worker
     * @return the number of tasks that worker should run
     */
    static int taskSubset(int numTasks, int numWorkers, int i) {
        return numTasks / numWorkers + ((i < numTasks % numWorkers) ? 1 : 0);
    }

    /**
     * [main] Checks that the specified numbers of workers and tasks are valid.
     * Called at the end of every `init(...)` function.
     * <p>
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
