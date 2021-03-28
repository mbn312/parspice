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
public abstract class OWorker<O> {

    /**
     * Creates an instance of a worker subclass and runs the tasks specified
     * by the CLI arguments.
     */
    public static void main(String[] args) throws Exception {
        OWorker<?> worker = (OWorker<?>) Class.forName(args[0]).getConstructor().newInstance();
        int inputPort = Integer.parseInt(args[1]);
        try {
            worker.setup();

            Socket outputSocket = new Socket("localhost", inputPort + 1);
            ObjectOutputStream oos = new ObjectOutputStream(outputSocket.getOutputStream());

            int startI = Integer.parseInt(args[2]);
            int subset = Integer.parseInt(args[3]);
            for (int i = startI; i < startI + subset; i++) {
                worker.taskWrapper(i, oos);
            }
            oos.close();
            outputSocket.close();
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
            FileWriter writer = new FileWriter("/tmp/worker_log_" + args[4]);
            writer.write(e.toString());
            writer.write("Was sending on port " + (inputPort + 1));
            PrintWriter printer = new PrintWriter(writer);
            e.printStackTrace(printer);
            printer.close();
            writer.flush();
            writer.close();
        }
    }

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
     * Get an instance of the output sender.
     *
     * @return instance of the output sender.
     */
    public final Sender<O> getOutputSender() {
        return outputSender;
    }

    /**
     * Called only once, before repeatedly calling {@code task(i)}.
     *
     * If you need to load a native library or perform any one-time preparation,
     * it should be done in this function. If not, you don't need to override it.
     *
     * All setup that might throw an error should be done here, not in the main
     * entry point of the worker; the call to setup is wrapped in a try/catch for error reporting.
     */
    public void setup() throws Exception {}

    /**
     * Handles the IO for a particular task iteration, and calls task.
     *
     * This function exists because the generic types can't be handled
     * in the main function without unchecked casts.
     *
     * @param i the integer to give to task(i)
     * @param oos the output stream to write the output to
     * @throws Exception
     */
    final void taskWrapper(int i, ObjectOutputStream oos) throws Exception {
        outputSender.write(task(i), oos);
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
