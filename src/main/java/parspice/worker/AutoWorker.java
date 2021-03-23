package parspice.worker;

import parspice.sender.Sender;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Superclass of all Worker tasks that DO take input arguments sent from
 * the main process. All subclasses should include a main entry point that
 * calls {@code run(...)} on an instance of themselves.
 */
public interface AutoWorker {

    /**
     * Runs the worker.
     *
     * This function handles the networking so it will not concern the user.
     * It first calls {@code setup()}, and then repeatedly calls {@code task(i)}
     * and sends the returned values back to the main process.
     *
     * Errors are printed to /tmp/worker_log_i where i is the worker's id.
     *
     * @param worker an instance of the worker to put to work.
     * @param args The CLI arguments given to the main function.
     *             These should not be modified in any way.
     * @throws Exception
     */
    static void run(AutoWorker worker, String[] args) throws IOException {
        try {
            worker.setup();

            int numIterations = Integer.parseInt(args[2]);
            for (int i = 0; i < numIterations; i++) {
                worker.task(i);
            }
        } catch (Exception e) {
            FileWriter writer = new FileWriter("/tmp/worker_log_" + args[3]);
            writer.write(e.toString());
            PrintWriter printer = new PrintWriter(writer);
            e.printStackTrace(printer);
            printer.close();
            writer.flush();
            writer.close();
        }
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
    default void setup() throws Exception {}

    /**
     * Called repeatedly, once for each integer {@code i} in the index range
     * given by the command line arguments.
     *
     * @param i The integer index of the task. It should be used to calculate
     *          the initial state or values needed by the task.
     *          The task receives no other indication of which iteration it is.
     * @throws Exception
     */
    void task(int i) throws Exception;
}
