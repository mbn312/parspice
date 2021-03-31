package parspice.worker;

import java.io.*;

/**
 * Superclass of all Worker tasks that don't take input arguments sent from
 * the main process, and do return outputs. All subclasses should include a main entry point that
 * calls {@code run(new This(), args) with an instance of themselves.
 *
 * @param <O> The type returned by the worker to the main process.
 */
public abstract class AutoWorker {

    /**
     * Creates an instance of a worker subclass and runs the tasks specified
     * by the CLI arguments.
     */
    public static void main(String[] args) throws Exception {
        int inputPort = Integer.parseInt(args[1]);
        try {
            AutoWorker worker = (AutoWorker) Class.forName(args[0]).getConstructor().newInstance();
            worker.setup();

            int startI = Integer.parseInt(args[2]);
            int subset = Integer.parseInt(args[3]);
            for (int i = startI; i < startI + subset; i++) {
                worker.task(i);
            }
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
     * Called repeatedly, once for each integer {@code i} in the index range
     * given by the command line arguments.
     *
     * @param i The integer index of the task. It should be used to calculate
     *          the initial state or values needed by the task.
     *          The task receives no other indication of which iteration it is.
     * @throws Exception
     */
    public abstract void task(int i) throws Exception;
}
