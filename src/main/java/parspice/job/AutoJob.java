package parspice.job;

import parspice.ParSPICE;
import static parspice.Worker.*;


/**
 * Superclass of all Worker tasks that don't take input arguments sent from
 * the main process, and do return outputs. All subclasses should include a main entry point that
 * calls {@code run(new This(), args) with an instance of themselves.
 */
public abstract class AutoJob extends Job<Void, Void, Void> {

    public final AutoJob init(int numWorkers, int numTasks) {
        this.numWorkers = numWorkers;
        this.numTasks = numTasks;

        validate();

        return this;
    }

    @Override
    public final void setupWrapper() throws Exception {
        setup();
    }

    /**
     * Repeatedly calls task.
     */
    @Override
    public final void taskWrapper() throws Exception {
        for (int i = getStartIndex(); i < getStartIndex() + getTaskSubset(); i++) {
            task(i);
        }
    }

    @Override
    public final void startConnections() {}

    @Override
    public final void endConnections() {}

    public final void run(ParSPICE par) throws Exception {
        runCommon(par, null);
    }

    public void setup() throws Exception {}

    /**
     * Called repeatedly, once for each integer {@code i} in the index range
     * given by the command line arguments.
     *
     * @param i The integer index of the task. It should be used to calculate
     *          the initial state or values needed by the task.
     *          The task receives no other indication of which iteration it is.
     * @throws Exception
     */
    public abstract void task(int i) throws Exception;
}
