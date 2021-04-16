package parspice.worker;

/**
 * Superclass of all Worker tasks that don't take input arguments sent from
 * the main process, and do return outputs. All subclasses should include a main entry point that
 * calls {@code run(new This(), args) with an instance of themselves.
 *
 * @param <O> The type returned by the worker to the main process.
 */
public abstract class AutoWorker extends Worker {

    /**
     * Repeatedly calls task.
     */
    public final void run() throws Exception {
        for (int i = startIndex; i < startIndex + subset; i++) {
            task(i);
        }
    }

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
