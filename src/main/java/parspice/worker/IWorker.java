package parspice.worker;

import parspice.sender.Sender;

import java.io.*;
import java.net.Socket;

/**
 * Superclass of all Worker tasks that take input arguments sent from
 * the main process, and return no output. All subclasses should include a main entry point that
 * calls {@code run(new This(), args)} with an instance of themselves.
 *
 * @param <I> The type input to the worker by the main process.
 */
public interface IWorker<I> {

    /**
     * Runs the worker.
     *
     * This function handles the networking so it will not concern the user.
     * It first calls {@code setup()}, and then repeatedly calls {@code task(T input)}.
     *
     * Errors are printed to /tmp/worker_log_i where i is the worker's id.
     *
     * @param worker an instance of the worker to put to work.
     * @param args The CLI arguments given to the main function.
     *             These should not be modified in any way.
     * @throws Exception
     */
    static <I> void run(IWorker<I> worker, String[] args) throws IOException {
        int inputPort = Integer.parseInt(args[0]);
        try {
            worker.setup();

            Sender<I> inputSender = worker.getInputSender();

            Socket inputSocket = new Socket("localhost", inputPort);

            ObjectInputStream ois = new ObjectInputStream(inputSocket.getInputStream());

            int subset = Integer.parseInt(args[2]);
            for (int i = 0; i < subset; i++) {
                worker.task(inputSender.read(ois));
            }
            ois.close();
            inputSocket.close();
        } catch (Exception e) {
            FileWriter writer = new FileWriter("/tmp/worker_log_" + args[3]);
            writer.write(e.toString());
            writer.write("Was receiving on port " + inputPort);
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
     * @throws Exception
     */
    void task(I input) throws Exception;
}
