package parspice.worker;

import parspice.ParSPICE;
import parspice.io.IOManager;
import parspice.io.IServer;
import parspice.io.OServer;
import parspice.sender.Sender;

import java.util.ArrayList;
import java.util.List;

/**
 * A Job is the actual computations the user needs to perform,
 * i.e. the Worker, combined with all of its inputs and configuration.
 * The user gets an instance of a Job by calling init on a Worker instance,
 * and then runs it with .run(par) (defined in subclasses of Job).
 *
 * The user can't create instances of jobs directly, as the constructors
 * are all package-private. Instead, the user can only get an instance
 * by calling init on a Worker instance.
 *
 * @param <S> Type for setup inputs (Void if none)
 * @param <I> Type for task inputs (Void if none)
 * @param <O> Type for task outputs (Void if none)
 */
public abstract class Job<S,I,O> {
    int numWorkers;
    int numTasks;

    /**
     * setup inputs supplied by the user
     */
    List<S> setupInputs;
    /**
     * inputs supplied by the user
     */
    List<I> inputs;

    Sender<S> setupSender;
    Sender<I> inputSender;
    Sender<O> outputSender;

    private final Worker<O> worker;

    /**
     * List io managers used in runCommon. It is stored
     * as a member field so that OJob can access it later.
     */
    protected ArrayList<IOManager<S, I, O>> ioManagers;

    Job(Worker<O> worker) {
        this.worker = worker;
    }

    /**
     * Common logic for running all jobs. Creates the IOManagers and worker processes,
     * runs them, and waits for them to finish.
     *
     * @param par Instance of ParSPICE to use
     * @throws Exception
     */
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
     * Calculate how many tasks should be given to a particular worker.
     *
     * Each worker is given an almost-equal taskSubset. If numTasks is not
     * an even multiple of numWorkers, the remainder is spread across the
     * first numTasks % numWorkers workers.
     *
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
     * Checks that the configuration given in the init functions is valid.
     *
     * @throws IllegalStateException if the state is not valid
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

        if (inputs == null ^ inputSender == null) {
            throw new IllegalStateException("Inputs and input sender must either be both null or both not null.");
        }
        if (setupInputs == null ^ setupSender == null) {
            throw new IllegalStateException("Setup inputs and setup sender must either be both null or both not null");
        }

        if (inputs != null && inputs.size() != numTasks) {
            throw new IllegalStateException("Inputs size should match numTasks. This is an internal error, not user error.");
        }
        if (setupInputs != null && setupInputs.size() != numWorkers) {
            throw new IllegalStateException("Setup inputs size should match numWorkers. This is an internal error, not user error.");
        }


    }
}
