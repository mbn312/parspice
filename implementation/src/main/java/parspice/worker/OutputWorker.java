package parspice.worker;

import parspice.sender.Sender;

import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Superclass of all Worker tasks that DON'T take input arguments sent from
 * the main process. All subclasses should include a main entry point that
 * calls {@code run(...)} on an instance of themselves.
 *
 * -- ex: A dumb vhat task --
 *
 * <pre>
 *     {@code
 * import parspice.outputSender.DoubleArraySender;
 * import spice.basic.CSPICE;
 * import spice.basic.SpiceErrorException;
 * import parspice.worker.NoInputWorker;
 *
 * public class VhatOutputWorker extends OutputWorker<double[]> {
 *     public static void main(String[] args) throws Exception {
 *         new VhatOutputWorker().run(
 *                 new DoubleArraySender(3),
 *                 args
 *         );
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
 * @param <T> The type returned by the worker to the main process.
 */
public abstract class OutputWorker<T> {

    /**
     * Runs the worker.
     *
     * This function handles the networking so it will not concern the user.
     * It first calls {@code setup()}, and then repeatedly calls {@code task(i)}
     * and sends the returned values back to the main process.
     *
     * @param outputSender The Sender responsible for sending the values returned by
     *               {@code task(i)} back to the main process.
     * @param args The CLI arguments given to the main function.
     *             These should not be modified in any way.
     * @throws Exception
     */
    public final void run(Sender<T> outputSender, String[] args) throws Exception {
        try {
            setup();

            Socket socket = new Socket("localhost", Integer.parseInt(args[0]));
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            int startI = Integer.parseInt(args[1]);
            int numIterations = Integer.parseInt(args[2]);
            for (int i = startI; i < startI + numIterations; i++) {
                outputSender.write(task(i), oos);
            }
            oos.close();
            socket.close();
        } catch (Exception e) {
            FileWriter writer = new FileWriter("/tmp/worker_log_" + args[0]);
            writer.write(e.toString());
            writer.flush();
            writer.close();
        }
    }

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
     * @param i The integer index of the task. It should be used to calculate
     *          the initial state or values needed by the task.
     *          The task receives no other indication of which iteration it is.
     * @return The value to be sent back to the main process.
     * @throws Exception
     */
    protected abstract T task(int i) throws Exception;
}
