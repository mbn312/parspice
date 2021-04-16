package parspice.worker;

import java.io.FileWriter;
import java.io.PrintWriter;

public abstract class Worker {
    static int workerID = 0;
    static int numWorkers = 1;
    static int numIterations = 1;
    static int inputPort = 0;
    static int outputPort = 1;
    static int startIndex = 0;
    static int subset = 1;

    public static void main(String[] args) throws Exception {
        Worker worker = (Worker) Class.forName(args[0]).getConstructor().newInstance();
        inputPort = Integer.parseInt(args[1]);
        outputPort = inputPort + 1;
        startIndex = Integer.parseInt(args[2]);
        subset = Integer.parseInt(args[3]);
        workerID = Integer.parseInt(args[4]);
        numWorkers = Integer.parseInt(args[5]);
        numIterations = Integer.parseInt(args[6]);
        try {
            worker.setup();
            worker.run();
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
            FileWriter writer = new FileWriter("/tmp/worker_log_" + args[4]);
            writer.write("Was receiving on port " + inputPort);
            writer.write("Was sending on port " + (inputPort + 1));
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
    public void setup() throws Exception {}

    public abstract void run() throws Exception;

    public static int getWorkerID() {
        return workerID;
    }

    public static int getNumWorkers() {
        return numWorkers;
    }

    public static int getInputPort() {
        return inputPort;
    }

    public static int getOutputPort() {
        return outputPort;
    }

    public static int getStartIndex() {
        return startIndex;
    }

    public static int getSubset() {
        return subset;
    }

    public static int getNumIterations() {
        return numIterations;
    }
}
