package parspice.worker;

import parspice.sender.Sender;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Superclass of all Worker tasks that don't take input arguments sent from
 * the main process, and do return outputs. All subclasses should include a main entry point that
 * calls {@code run(new This(), args) with an instance of themselves.
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
 *     public static void main(String[] args) throws Exception {
 *         new VhatOutputWorker().run(args);
 *     }
 *
 *     @Override
 *     public Sender<double[]> getOutputSender() { return new DoubleArraySender(3); }
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
public interface OWorker<O> {

    /**
     * Runs the worker.
     *
     * This function handles the networking so it will not concern the user.
     * It first calls {@code setup()}, and then repeatedly calls {@code task(int i)}
     * and sends the returned values back to the main process.
     *
     * Errors are printed to /tmp/worker_log_i where i is the worker's id.
     *
     * @param worker an instance of the worker to put to work.
     * @param args The CLI arguments given to the main function.
     *             These should not be modified in any way.
     */
    static <O> void run(OWorker<O> worker, String[] args) throws IOException {
        int inputPort = Integer.parseInt(args[0]);
        try {
            worker.setup();

            Sender<O> outputSender = worker.getOutputSender();

            Socket outputSocket = new Socket("localhost", inputPort + 1);
            ObjectOutputStream oos = new ObjectOutputStream(outputSocket.getOutputStream());

            int startI = Integer.parseInt(args[1]);
            int subset = Integer.parseInt(args[2]);
            for (int i = startI; i < startI + subset; i++) {
                outputSender.write(worker.task(i), oos);
            }
            oos.close();
            outputSocket.close();
        } catch (Exception e) {
            FileWriter writer = new FileWriter("/tmp/worker_log_" + args[3]);
            writer.write(e.toString());
            writer.write("Was sending on port " + (inputPort + 1));
            PrintWriter printer = new PrintWriter(writer);
            e.printStackTrace(printer);
            printer.close();
            writer.flush();
            writer.close();
        }
    }

    /**
     * Get an instance of the output sender.
     *
     * @return instance of the output sender.
     */
    Sender<O> getOutputSender();

    /**
     * Called only once, before repeatedly calling {@code task(i)}.
     *
     * If you need to load a native library or perform any one-time preparation,
     * it should be done in this function. If not, you don't need to override it.
     *
     * All setup that might throw an error should be done here, not in the main
     * entry point of the worker; the call to setup is wrapped in a try/catch for error reporting.
     */
    default void setup() throws Exception {}

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
    O task(int i) throws Exception;
}
