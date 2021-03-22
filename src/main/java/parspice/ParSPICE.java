package parspice;

import parspice.sender.Sender;
import parspice.socketManager.IOSocketManager;
import parspice.socketManager.OSocketManager;
import parspice.socketManager.SocketManager;
import parspice.worker.IOWorker;
import parspice.worker.OWorker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * The interface for running parallel multi-process tasks with ParSPICE.
 *
 * Tasks can be run either with input arguments given to the worker processes, or without.
 * For short tasks, the network overhead is significant, so input arguments should be
 * avoided if at all possible. If inputs are not used, the task will receive an integer
 * indicating which run of the task it is, as if it was inside a for loop on a single process.
 */
public class ParSPICE {

    private final String workerJar;
    private final int minPort;

    /**
     * Creates a new ParSPICE object with for a given jar file.
     *
     * @param workerJar the jar for all tasks on this instance to be
     *                  run from.
     */
    public ParSPICE(String workerJar, int minPort) throws IOException {
        checkJar(workerJar);

        this.workerJar = workerJar;
        this.minPort = minPort;
    }

    /**
     * Runs a custom task that takes remote inputs and returns outputs
     * to the main process.
     *
     * Sending inputs is slow. The network overhead of both sending inputs and
     * receiving outputs is slightly more than double the overhead of just
     * receiving outputs. Prefer the output-only version if at all possible.
     *
     * @param ioWorker An instance of the worker to parallelize
     * @param inputs List of inputs to be sent and processed in parallel
     * @param numWorkers Number of worker processes to distribute to
     * @param <I> Input argument type
     * @param <O> Output return type
     * @return The list of outputs, in the same order as the inputs
     * @throws Exception
     */
    public <I,O> List<O> run(
            IOWorker<I,O> ioWorker,
            List<I> inputs,
            int numWorkers
    ) throws Exception {
        String mainClass = ioWorker.getClass().getName();
        Sender<I> inputSender = ioWorker.getInputSender();
        Sender<O> outputSender = ioWorker.getOutputSender();
        List<SocketManager<O>> socketManagers = new ArrayList<>(numWorkers);
        int numIterations = inputs.size();
        int iteration = 0;
        for (int i = 0; i < numWorkers; i++) {
            int subset = subset(numIterations, numWorkers, i);
            socketManagers.add(new IOSocketManager<I, O>(
                    new ServerSocket(minPort + i),
                    inputs.subList(iteration, iteration + subset),
                    inputSender,
                    outputSender,
                    i
            ));
            iteration += subset;
        }
        return run(workerJar, mainClass, minPort, numIterations, numWorkers, socketManagers);
    }

    /**
     * Runs a custom task that takes a locally-generated integer as input
     * and returns outputs to the main process.
     *
     * @param oWorker An instance of the worker to parallelize.
     * @param numIterations Number of times to run the task. Each run will receive as
     *                      argument a unique index i in the range 0:(numIterations-1) (inclusive)
     * @param numWorkers Number of worker processes to distribute to
     * @param <O> Output return type
     * @return The list of outputs, sorted by index i. (see argument numIterations)
     * @throws Exception
     */
    public <O> List<O> run(
            OWorker<O> oWorker,
            int numIterations,
            int numWorkers
    ) throws Exception {
        String mainClass = oWorker.getClass().getName();
        Sender<O> outputSender = oWorker.getOutputSender();
        List<SocketManager<O>> socketManagers = new ArrayList<>(numWorkers);
        for (int i = 0; i < numWorkers; i++) {
            int subset = subset(numIterations, numWorkers, i);
            socketManagers.add(new OSocketManager<>(
                    new ServerSocket(minPort + i),
                    outputSender,
                    i,
                    subset
            ));
        }
        return run(workerJar, mainClass, minPort, numIterations, numWorkers, socketManagers);
    }

    /**
     * Internal logic common to both of the two publicly facing run functions.
     */
    private static <O> List<O> run(String workerJar, String mainClass, int minPort, int numIterations, int numWorkers, List<SocketManager<O>> socketManagers) throws Exception {
        checkMainClass(workerJar, mainClass);

        List<O> results = new ArrayList<>(numIterations);
        Process[] processes = new Process[numWorkers];
        int iteration = 0;
        for (int i = 0; i < numWorkers; i++) {
            int subset = subset(numIterations, numWorkers, i);
            String args = workerJar + " " + mainClass + " " + (minPort + i) + " " + iteration + " " + subset;
            socketManagers.get(i).start();
            processes[i] = Runtime.getRuntime().exec("java -cp " + args);
            iteration += subset;
        }
        for (int i = 0; i < numWorkers; i++) {
            socketManagers.get(i).join();
            processes[i].waitFor();
            results.addAll(socketManagers.get(i).getOutputs());
        }
        return results;
    }

    /**
     * Checks that the given file exists, and that it is a .jar file. Throws an exception if not.
     *
     * @param workerJar path (can be relative) to the jar file.
     * @throws IOException
     */
    private static void checkJar(String workerJar) throws IOException {
        String extension = workerJar.substring(workerJar.length()-4);
        if (!extension.equals(".jar")) {
            throw new IOException("workerJar must be a .jar file, not: " + extension);
        }
        File file = new File(workerJar);
        if (!file.exists()) {
            throw new FileNotFoundException(workerJar);
        }
    }

    /**
     * Checks that the given main class is in the the jar file. Throws an exception if not.
     *
     * @param workerJar path (can be relative) to the jar file
     * @param mainClass main class to look for, in package notation as given as a jvm argument.
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static void checkMainClass(String workerJar, String mainClass) throws ClassNotFoundException, IOException {
        JarFile jarFile = new JarFile(workerJar);
        Enumeration<JarEntry> e = jarFile.entries();
        while (e.hasMoreElements()) {
            JarEntry jarEntry = e.nextElement();
            if (jarEntry.getName().endsWith(".class")) {
                String className = jarEntry.getName()
                        .replace("/", ".")
                        .replace(".class", "");
                if (className.equals(mainClass)) {
                    return;
                }
            }
        }
        throw new ClassNotFoundException(mainClass);
    }

    /**
     * Calculate how many iterations should be given to a particular worker.
     *
     * Each worker is given an almost-equal subset. If numIterations is not
     * an even multiple of numWorkers, the remainder is spread across the
     * first numIterations % numWorkers workers.
     *
     * @param numIterations total number of iterations
     * @param numWorkers number of workers
     * @param i the index of a particular worker
     * @return the number of iterations that worker should run
     */
    private static int subset(int numIterations, int numWorkers, int i) {
        return numIterations/numWorkers + ((i < numIterations%numWorkers)?1:0);
    }
}
