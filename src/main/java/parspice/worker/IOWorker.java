package parspice.worker;

import parspice.sender.Sender;

import java.io.*;
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
public abstract class IOWorker<I,O> {

    /**
     * Creates an instance of a worker subclass and runs the tasks specified
     * by the CLI arguments.
     */
    public static void main(String[] args) throws Exception {
        int inputPort = Integer.parseInt(args[1]);
        try {
            IOWorker<?,?> worker = (IOWorker<?,?>) Class.forName(args[0]).getConstructor().newInstance();
            worker.setup();

            Socket inputSocket = new Socket("localhost", inputPort);
            Socket outputSocket = new Socket("localhost", inputPort + 1);
            ObjectInputStream ois = new ObjectInputStream(inputSocket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(outputSocket.getOutputStream());

            int startI = Integer.parseInt(args[2]);
            int subset = Integer.parseInt(args[3]);
            for (int i = startI; i < startI + subset; i++) {
                worker.taskWrapper(ois, oos);
            }
            oos.close();
            outputSocket.close();
            ois.close();
            inputSocket.close();
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

    private final Sender<I> inputSender;
    private final Sender<O> outputSender;

    public IOWorker(Sender<I> inputSender, Sender<O> outputSender) {
        this.inputSender = inputSender;
        this.outputSender = outputSender;
    }

    /**
     * Get an instance of the input sender.
     *
     * @return instance of the input sender.
     */
    public Sender<I> getInputSender() {
        return inputSender;
    }

    /**
     * Get an instance of the output sender.
     *
     * @return instance of the output sender.
     */
    public Sender<O> getOutputSender() {
        return outputSender;
    }

    /**
     * Called only once, before repeatedly calling {@code task(input)}.
     *
     * If you need to load a native library or perform any one-time preparation,
     * it should be done in this function. If not, you don't need to override it.
     *
     * All setup that might throw an error should be done here, not in the main
     * entry point of the worker; the call to setup is wrapped in a try/catch for error reporting.
     */
    public void setup() throws Exception {}

    final void taskWrapper(ObjectInputStream ois, ObjectOutputStream oos) throws Exception {
        outputSender.write(task(inputSender.read(ois)), oos);
    }

    /**
     * Called repeatedly, once for each integer {@code i} in the index range
     * given by the command line arguments.
     *
     * @param input The input given by the main process to the worker.
     * @return The value to be sent back to the main process.
     * @throws Exception
     */
    public abstract O task(I input) throws Exception;
}
