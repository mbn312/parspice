package parspice;

import parspice.job.Job;

import java.io.FileWriter;
import java.io.PrintWriter;

public class Worker {
    /**
     * Unique ID for the worker, in the range [0, numWorkers)
     */
    private static int workerID = 0;

    /**
     * Total number of workers in this job.
     */
    private static int numWorkers = 1;

    /**
     * Total number of iterations to be run.
     */
    private static int numTasks = 1;

    /**
     * Port used to receive inputs.
     */
    private static int inputPort = 0;

    /**
     * Port used to send outputs.
     */
    private static int outputPort = 1;

    /**
     * Iteration index that this worker starts at.
     */
    private static int startIndex = 0;

    public static int getWorkerID() {
        return workerID;
    }

    public static int getNumWorkers() {
        return numWorkers;
    }

    public static int getInputPort() {
        return inputPort;
    }

    public static int getNumTasks() {
        return numTasks;
    }

    public static int getOutputPort() {
        return outputPort;
    }

    public static int getStartIndex() {
        return startIndex;
    }

    public static int getTaskSubset() {
        return taskSubset;
    }

    /**
     * How many tasks this worker needs to run.
     */
    private static int taskSubset = 1;

    public static void main(String[] args) throws Exception {
        Job<?,?,?> job = (Job<?,?,?>) Class.forName(args[0]).getConstructor().newInstance();
        try {
            inputPort = Integer.parseInt(args[1]);
            outputPort = inputPort + 1;
            startIndex = Integer.parseInt(args[2]);
            taskSubset = Integer.parseInt(args[3]);
            workerID = Integer.parseInt(args[4]);
            numWorkers = Integer.parseInt(args[5]);
            numTasks = Integer.parseInt(args[6]);

            job.startConnections();
            job.setupWrapper();
            job.taskWrapper();
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();

            FileWriter writer = new FileWriter("ParSPICE_worker_log_" + workerID + ".txt");

            writer.write("workerName\t" + args[0]);
            writer.write("\ninputPort\t" + inputPort);
            writer.write("\noutputPort\t" + outputPort);
            writer.write("\nstartIndex\t" + startIndex);
            writer.write("\ntaskSubset\t" + taskSubset);
            writer.write("\nworkerID\t" + workerID);
            writer.write("\nnumWorkers\t" + numWorkers);
            writer.write("\nnumTasks\t" + numTasks + "\n\n");

            writer.write(e.toString());
            writer.write("\n\n");
            PrintWriter printer = new PrintWriter(writer);
            e.printStackTrace(printer);
            printer.close();
            writer.flush();
            writer.close();
        } finally {
            job.endConnections();
        }
    }
}
