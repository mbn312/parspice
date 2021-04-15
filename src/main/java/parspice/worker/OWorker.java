package parspice.worker;

import parspice.sender.Sender;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

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
public abstract class OWorker<O> extends Worker {

    private final Sender<O> outputSender;

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
     * Creates an instance of a worker subclass and runs the tasks specified
     * by the CLI arguments.
     */
    @Override
    public final void run() throws Exception {
        Socket outputSocket = new Socket("localhost", inputPort + 1);
        ObjectOutputStream oos = new ObjectOutputStream(outputSocket.getOutputStream());

        for (int i = startIndex; i < startIndex + subset; i++) {
            outputSender.write(task(i), oos);
        }
        oos.close();
        outputSocket.close();
    }

    /**
     * Get an instance of the output sender.
     *
     * @return instance of the output sender.
     */
    public final Sender<O> getOutputSender() {
        return outputSender;
    }

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
