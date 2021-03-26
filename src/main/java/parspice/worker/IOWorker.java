package parspice.worker;

import parspice.sender.Sender;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Superclass of all Worker tasks that take input arguments sent from
 * the main process, and return outputs back. All subclasses should include a main entry point that
 * calls {@code run(new This(), args)} with an instance of themselves.
 *
 * -- ex: A vhat task --
 *
 * <pre>
 *     {@code
 * import parspice.sender.DoubleArraySender;
 * import spice.basic.CSPICE;
 * import spice.basic.SpiceErrorException;
 * import parspice.worker.InputOutputWorker;
 *
 * public class VhatInputOutputWorker extends InputOutputWorker<double[]> {
 *     public static void main(String[] args) throws Exception {
 *         new VhatInputOutputWorker().run(args);
 *     }
 *
 *     @Override
 *     public Sender<double[]> getInputSender() { return new DoubleArraySender(3); }
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
 *     public double[] task(double[] in) throws SpiceErrorException {
 *         return CSPICE.vhat(in);
 *     }
 * }
 *     }
 * </pre>
 *
 * @param <I> The type input to the worker by the main process.
 * @param <O> The type returned by the worker to the main process.
 */
public interface IOWorker<I, O> {

    /**
     * Runs the worker.
     *
     * This function handles the networking so it will not concern the user.
     * It first calls {@code setup()}, and then repeatedly calls {@code task(i)}
     * and sends the returned values back to the main process.
     *
     * Errors are printed to /tmp/worker_log_i where i is the worker's id.
     *
     * @param worker An instance of the worker to put to work.
     * @param args The CLI arguments given to the main function.
     *             These should not be modified in any way.
     * @throws Exception
     */
    static <I,O> void run(IOWorker<I,O> worker, String[] args) throws IOException {
        int inputPort = Integer.parseInt(args[0]);
        try {
            worker.setup();

            Sender<I> inputSender = worker.getInputSender();
            Sender<O> outputSender = worker.getOutputSender();

            Socket inputSocket = new Socket("localhost", inputPort);
            Socket outputSocket = new Socket("localhost", inputPort + 1);

            ObjectOutputStream oos = new ObjectOutputStream(outputSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(inputSocket.getInputStream());

            int subset = Integer.parseInt(args[2]);
            for (int i = 0; i < subset; i++) {
                outputSender.write(worker.task(inputSender.read(ois)), oos);
            }
            oos.close();
            ois.close();
            inputSocket.close();
            outputSocket.close();
        } catch (Exception e) {
            FileWriter writer = new FileWriter("/tmp/worker_log_" + args[3]);
            writer.write(e.toString());
            writer.write("Was receiving on port " + inputPort + " and sending on port " + (inputPort + 1));
            PrintWriter printer = new PrintWriter(writer);
            e.printStackTrace(printer);
            printer.close();
            writer.flush();
            writer.close();
        }
    }

    /**
     * Get an instance of the input sender.
     *
     * @return instance of the input sender.
     */
    Sender<I> getInputSender();

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
     * @param input The input given to the task by the main process.
     * @return The value to be sent back to the main process.
     * @throws Exception
     */
    O task(I input) throws Exception;
}
