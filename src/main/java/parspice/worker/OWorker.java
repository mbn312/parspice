package parspice.worker;

import parspice.ParSPICE;
import parspice.io.IOManager;
import parspice.io.OServer;
import parspice.sender.Sender;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Superclass of all Worker tasks that don't take input arguments sent from
 * the main process, and do return outputs.
 *
 * -- ex: A simple vhat task --
 *
 * <pre>
 *     {@code
 * import parspice.sender.DoubleArraySender;
 * import spice.basic.CSPICE;
 * import spice.basic.SpiceErrorException;
 * import parspice.job.OJob;
 *
 * public class VhatOutputJob extends OJob<double[]> {
 *     public VhatOutputJob() {
 *         super(new DoubleArraySender(3));
 *     }
 *
 *     @Override
 *     public void setup() {
 *         System.loadLibrary("JNISpice");
 *     }
 *
 *     @Override
 *     public double[] task(int i) throws SpiceErrorException {
 *         return CSPICE.vhat(new double[]{1, 2, i});
 *     }
 * }
 *     }
 * </pre>
 *
 * @param <O> The type returned by the worker to the main process.
 */
public abstract class OWorker<O> extends Worker<O> {

    private final Sender<O> outputSender;

    private Socket outputSocket;
    private ObjectOutputStream oos;

    /**
     * Creates a new OWorker instance
     *
     * @param outputSender the sender used to sender output results back to
     *                     the main process
     */
    public OWorker(Sender<O> outputSender) {
        this.outputSender = outputSender;
    }

    /**
     * [main process] Initialize the job with the inputs it needs to run.
     *
     * @param numWorkers number of workers to use.
     * @param numTasks number of tasks to run.
     * @return this (builder pattern)
     */
    public final OJob<Void,Void,O> init(int numWorkers, int numTasks) {
        OJob<Void,Void,O> job = new OJob<>(this);

        job.numWorkers = numWorkers;
        job.numTasks = numTasks;
        job.outputSender = outputSender;

        job.validate();

        return job;
    }

    /**
     * [worker process] Calls setup.
     *
     * The user cannot call or override this function.
     *
     * @throws Exception any exception the user code needs to throw
     */
    @Override
    final void setupWrapper() throws Exception {
        setup();
    }

    /**
     * [worker process] Repeatedly calls task and writes the output to stream
     *
     * The user cannot call or override this function.
     *
     * @throws Exception any exception the user code needs to throw
     */
    @Override
    final void taskWrapper() throws Exception {
        for (int i = getStartIndex(); i < getStartIndex() + getTaskSubset(); i++) {
            outputSender.write(task(i), oos);
        }
    }

    /**
     * [worker process] Starts the output socket connection with the main process.
     *
     * @throws IOException if the connection cannot be made
     */
    @Override
    final void startConnections() throws IOException {
        outputSocket = new Socket("localhost", getOutputPort());
        oos = new ObjectOutputStream(outputSocket.getOutputStream());
    }

    /**
     * [worker process] Ends the output connection with the main process.
     *
     * @throws IOException if the connection cannot be ended.
     */
    @Override
    final void endConnections() throws IOException {
        oos.close();
        outputSocket.close();
    }

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
     * given by the command line arguments.
     *
     * @param i The integer index of the task. It should be used to calculate
     *          the initial state or values needed by the task.
     *          The task receives no other indication of which iteration it is.
     * @return The value to be sent back to the main process.
     * @throws Exception any exception the user code needs to throw
     */
    public abstract O task(int i) throws Exception;
}
