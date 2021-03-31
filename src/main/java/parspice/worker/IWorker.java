package parspice.worker;

import parspice.sender.Sender;

import java.io.*;
import java.net.Socket;

/**
 * Superclass of all Worker tasks that don't take input arguments sent from
 * the main process, and do return outputs.
 *
 * @param <I> The type given to the worker by the main process.
 */
public abstract class IWorker<I> {

    /**
     * Creates an instance of a worker subclass and runs the tasks specified
     * by the CLI arguments.
     */
    public static void main(String[] args) throws Exception {
        int inputPort = Integer.parseInt(args[1]);
        try {
            IWorker<?> worker = (IWorker<?>) Class.forName(args[0]).getConstructor().newInstance();
            worker.setup();

            Socket inputSocket = new Socket("localhost", inputPort);
            ObjectInputStream ois = new ObjectInputStream(inputSocket.getInputStream());

            int startI = Integer.parseInt(args[2]);
            int subset = Integer.parseInt(args[3]);
            for (int i = startI; i < startI + subset; i++) {
                worker.taskWrapper(ois);
            }
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

    public IWorker(Sender<I> inputSender) {
        this.inputSender = inputSender;
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
     * Called only once, before repeatedly calling {@code task(input)}.
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
     * @param ois the input stream to read from
     * @throws Exception
     */
    final void taskWrapper(ObjectInputStream ois) throws Exception {
        task(inputSender.read(ois));
    }

    /**
     * Called repeatedly, once for each input sent from the main process.
     *
     * @param input The input given by the main process to the worker.
     * @throws Exception
     */
    public abstract void task(I input) throws Exception;
}
