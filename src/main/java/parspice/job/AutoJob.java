package parspice.job;

import parspice.ParSPICE;

/**
 * Superclass of all Jobs that don't take input arguments sent from
 * the main process, and do return outputs.
 */
public abstract class AutoJob extends Job<Void> {

    /**
     * [main process] Initialize the job with the inputs it needs to run.
     *
     * @param numWorkers number of workers to use.
     * @param numTasks number of tasks to run.
     * @return this (builder pattern)
     */
    public final AutoJob init(int numWorkers, int numTasks) {
        this.numWorkers = numWorkers;
        this.numTasks = numTasks;

        validate();

        return this;
    }

    /**
     * [main process] Runs the job in parallel.
     *
     * @param par a ParSPICE instance with worker jar and minimum port number.
     * @throws Exception
     */
    public final void run(ParSPICE par) throws Exception {
        runCommon(par, null);
    }

    /**
     * [worker process] Calls setup.
     *
     * The user cannot call or override this function.
     *
     * @throws Exception
     */
    @Override
    final void setupWrapper() throws Exception {
        setup();
    }

    /**
     * [worker process] Repeatedly calls task.
     *
     * The user cannot call or override this function.
     */
    @Override
    final void taskWrapper() throws Exception {
        for (int i = getStartIndex(); i < getStartIndex() + getTaskSubset(); i++) {
            task(i);
        }
    }

    /**
     * [worker] Does nothing (AutoJobs have no connections to start).
     */
    @Override
    final void startConnections() {}

    /**
     * [worker] Does nothing (AutoJobs have no connections to end).
     */
    @Override
    final void endConnections() {}

    /**
     * [worker] Called once on each worker when the job starts running.
     *
     * The user can optionally override this function; by default it does nothing.
     *
     * @throws Exception any exception the user code needs to throw
     */
    public void setup() throws Exception {}

    /**
     * Called repeatedly, once for each integer {@code i} in the index range
     * given by the command line arguments. The user must implement this function.
     *
     * @param i The integer index of the task. It should be used to calculate
     *          the initial state or values needed by the task.
     *          The task receives no other indication of which iteration it is.
     * @throws Exception any exception the user code needs to throw
     */
    public abstract void task(int i) throws Exception;
}
