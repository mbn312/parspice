package parspice.job;

import parspice.ParSPICE;
import parspice.io.IOManager;
import parspice.io.IServer;
import parspice.io.OServer;
import parspice.sender.Sender;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static parspice.Worker.*;

/**
 * Superclass of all Worker tasks that don't take input arguments sent from
 * the main process, and do return outputs.
 *
 * -- ex: A dumb vhat task --
 *
 * <pre>
 *     {@code
 * import parspice.outputSender.DoubleArraySender;
 * import spice.basic.CSPICE;
 * import spice.basic.SpiceErrorException;
 * import parspice.worker.OWorker;
 *
 * public class VhatOutputWorker extends OWorker<double[]> {
 *     public VhatOutputWorker() {
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
public abstract class OJob<O> extends Job<Void, Void, O> {

    private final Sender<O> outputSender;

    private Socket outputSocket;
    private ObjectOutputStream oos;

    public final OJob<O> init(int numWorkers, int numTasks) {
        this.numWorkers = numWorkers;
        this.numTasks = numTasks;

        validate();

        return this;
    }

    /**
     * Creates a new OWorker instance
     *
     * @param outputSender the sender used to sender output results back to
     *                     the main process
     */
    public OJob(Sender<O> outputSender) {
        this.outputSender = outputSender;
    }

    @Override
    public final void setupWrapper() throws Exception {
        setup();
    }

    /**
     * Prepares the output stream and repeatedly calls task.
     */
    @Override
    public final void taskWrapper() throws Exception {
        for (int i = getStartIndex(); i < getStartIndex() + getTaskSubset(); i++) {
            outputSender.write(task(i), oos);
        }
    }

    @Override
    public final void startConnections() throws Exception {
        outputSocket = new Socket("localhost", getOutputPort());
        oos = new ObjectOutputStream(outputSocket.getOutputStream());
    }

    @Override
    public final void endConnections() throws Exception {
        oos.close();
        outputSocket.close();
    }

    public final ArrayList<O> run(ParSPICE par) throws Exception {
        ArrayList<IOManager<Void,Void,O>> ioManagers = new ArrayList<>(numWorkers);

        int task = 0;
        int minPort = par.getMinPort();

        for (int i = 0; i < numWorkers; i++) {
            int taskSubset = taskSubset(numTasks, numWorkers, i);
            OServer<O> oServer = new OServer<>(outputSender, taskSubset, minPort + 2*i + 1, i);
            ioManagers.add(new IOManager<>(null, oServer, i));
            task += taskSubset;
        }

        runCommon(par, ioManagers);

        return aggregateOutputs(ioManagers);
    }

    public void setup() throws Exception {}

    /**
     * Called repeatedly, once for each integer {@code i} in the index range
     * given by the command line arguments.
     *
     * @param i The integer index of the task. It should be used to calculate
     *          the initial state or values needed by the task.
     *          The task receives no other indication of which iteration it is.
     * @return The value to be sent back to the main process.
     * @throws Exception
     */
    public abstract O task(int i) throws Exception;
}
