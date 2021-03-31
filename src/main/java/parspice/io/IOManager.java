package parspice.io;

import java.util.ArrayList;

/**
 * Manages a pair of threads, one sending inputs, one receiving outputs.
 *
 * This class can handle any combination of IO: input only, output only,
 * both, or neither.
 *
 * @param <I> the input type argument for the task, or Void if none
 * @param <O> the output type returned by the task, or Void if none
 */
public class IOManager<I, O> {

    private final IServer<I> iServer;
    private final OServer<O> oServer;

    private Thread iThread;
    private Thread oThread;

    private final int workerIndex;

    /**
     * Create an instance of IOManager
     *
     * @param iServer an input port for sending inputs, or null if none
     * @param oServer an output port for receiving outputs, or null if none
     * @param workerIndex the index of the worker, for error reporting
     */
    public IOManager(IServer<I> iServer, OServer<O> oServer, int workerIndex) {
        this.iServer = iServer;
        this.oServer = oServer;
        this.workerIndex = workerIndex;
    }

    /**
     * Starts the iServer and oServer threads, if they exist.
     */
    public void start() {
        if (iServer != null && iThread == null) {
            iThread = new Thread(iServer, "input runnable " + workerIndex);
            iThread.start();
        }

        if (oServer != null && oThread == null) {
            oThread = new Thread(oServer, "output runnable " + workerIndex);
            oThread.start();
        }
    }

    /**
     * Waits for both threads to finish.
     *
     * @throws Exception
     */
    public void join() throws Exception {
        if (iThread != null) {
            iThread.join();
        }
        if (oThread != null) {
            oThread.join();
        }
    }

    /**
     * Gets the outputs from the oServer.
     *
     * This will throw a NullPointerException if called for a task that
     * doesn't give outputs. Don't do that.
     *
     * @return the outputs from the worker.
     */
    public ArrayList<O> getOutputs() {
        return oServer.getOutputs();
    }
}
