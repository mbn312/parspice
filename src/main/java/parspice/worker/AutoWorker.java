package parspice.worker;

import parspice.Job;
import parspice.io.IOManager;
import parspice.sender.Sender;

import java.util.ArrayList;

/**
 * Superclass of all Worker tasks that don't take input arguments sent from
 * the main process, and do return outputs. All subclasses should include a main entry point that
 * calls {@code run(new This(), args) with an instance of themselves.
 */
public abstract class AutoWorker extends Worker<Void, Void, Void> {

    @Override
    public final void setupWrapper() throws Exception {
        setup();
    }
    /**
     * Repeatedly calls task.
     */
    @Override
    public final void taskWrapper() throws Exception {
        for (int i = startIndex; i < startIndex + taskSubset; i++) {
            task(i);
        }
    }

    @Override
    public final void startConnections() {}

    @Override
    public final void endConnections() {}

    @Override
    public final Job<Void,Void,Void> job() {
        return new Job<>(this);
    }


    @Override
    public final Sender<Void> getOutputSender() {
        return null;
    }

    @Override
    public final Sender<Void> getSetupInputSender() {
        return null;
    }

    @Override
    public final Sender<Void> getInputSender() {
        return null;
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
