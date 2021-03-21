package parspice.worker;

import parspice.sender.Sender;

import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Superclass of all Worker tasks that DO take input arguments sent from
 * the main process. All subclasses should include a main entry point that
 * calls {@code run(...)} on an instance of themselves.
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
public abstract class InputOutputWorker<I, O> {

    /**
     * Runs the worker.
     *
     * This function handles the networking so it will not concern the user.
     * It first calls {@code setup()}, and then repeatedly calls {@code task(i)}
     * and sends the returned values back to the main process.
     *
     * @param args The CLI arguments given to the main function.
     *             These should not be modified in any way.
     * @throws Exception
     */
    public final void run(String[] args) throws Exception {
        FileWriter writer = new FileWriter("/tmp/worker_log_" + args[0]);
        try {
            setup();

            Sender<I> inputSender = getInputSender();
            Sender<O> outputSender = getOutputSender();

            Socket socket = new Socket("localhost", Integer.parseInt(args[0]));
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            int numIterations = Integer.parseInt(args[2]);
            List<I> inputs = new ArrayList<>(numIterations);
            for (int i = 0; i < numIterations; i++) {
                inputs.add(inputSender.read(ois));
            }
            for (int i = 0; i < numIterations; i++) {
                outputSender.write(task(inputs.get(i)), oos);
            }
            oos.close();
            ois.close();
            socket.close();
        } catch (Exception e) {
            writer.write(e.toString());
            writer.write(Arrays.toString(e.getStackTrace()));
            writer.flush();
        }
        writer.close();
    }

    /**
     * Get an instance of the input sender.
     *
     * @return instance of the input sender.
     */
    public abstract Sender<I> getInputSender();

    /**
     * Get an instance of the output sender.
     *
     * @return instance of the output sender.
     */
    public abstract Sender<O> getOutputSender();

    /**
     * Called only once, before repeatedly calling {@code task(i)}.
     *
     * If you need to load a native library or perform any one-time preparation,
     * it should be done in this function. If not, you don't need to override it.
     */
    protected void setup() {}

    /**
     * Called repeatedly, once for each integer {@code i} in the index range
     * given by the command line arguments.
     *
     * @param input The input given to the task by the main process.
     * @return The value to be sent back to the main process.
     * @throws Exception
     */
    protected abstract O task(I input) throws Exception;
}
