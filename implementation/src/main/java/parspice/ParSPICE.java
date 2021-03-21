package parspice;

import parspice.sender.Sender;
import parspice.socketManager.InputSocketManager;
import parspice.socketManager.NoInputSocketManager;

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
public class ParSPICE<I, O> {
    private final String workerJar;
    private final String mainClass;
    private final Sender<I> inputSender;
    private final Sender<O> outputSender;

    public ParSPICE(String workerJar, String mainClass, Sender<I> inputSender, Sender<O> outputSender) throws IOException, ClassNotFoundException {
        this.workerJar = workerJar;
        this.mainClass = mainClass;
        this.inputSender = inputSender;
        this.outputSender = outputSender;

        checkJar(workerJar, mainClass);
    }

    public ParSPICE(String workerJar, String mainClass, Sender<O> outputSender) throws IOException, ClassNotFoundException {
        this.workerJar = workerJar;
        this.mainClass = mainClass;
        this.inputSender = null;
        this.outputSender = outputSender;

        checkJar(workerJar, mainClass);
    }

    public List<O> run(List<I> inputs, int numWorkers) throws Exception {
        int iterations = inputs.size();
        List<O> results = new ArrayList<>(iterations);
        Process[] processes = new Process[numWorkers];
        List<InputSocketManager<I,O>> sockets = new ArrayList<>(numWorkers);
        int iteration = 0;
        for (int i = 0; i < numWorkers; i++) {
            int subset = subset(iterations, numWorkers, i);
            String args = workerJar + " " + mainClass + " " + (50050 + i) + " " + subset;
            sockets.add(new InputSocketManager<I, O>(
                    new ServerSocket(50050 + i),
                    inputs.subList(iteration, iteration + subset),
                    inputSender,
                    outputSender,
                    i
            ));
            sockets.get(i).start();
            processes[i] = Runtime.getRuntime().exec("java -cp " + args);
            iteration += subset;
        }
        for (int i = 0; i < numWorkers; i++) {
            sockets.get(i).join();
            processes[i].waitFor();
            results.addAll(sockets.get(i).getOutputs());
        }
        return results;
    }

    public List<O> run(int iterations, int numWorkers) throws Exception {
        List<O> results = new ArrayList<>(iterations);
        Process[] processes = new Process[numWorkers];
        List<NoInputSocketManager<O>> sockets = new ArrayList<>(numWorkers);
        int iteration = 0;
        for (int i = 0; i < numWorkers; i++) {
            int subset = subset(iterations, numWorkers, i);
            String args = workerJar + " " + mainClass + " " + (50050 + i) + " " + iteration + " " + subset;
            sockets.add(new NoInputSocketManager<>(
                    new ServerSocket(50050 + i),
                    outputSender,
                    i,
                    subset
            ));
            sockets.get(i).start();
            processes[i] = Runtime.getRuntime().exec("java -cp " + args);
            iteration += subset;
        }
        for (int i = 0; i < numWorkers; i++) {
            sockets.get(i).join();
            processes[i].waitFor();
            results.addAll(sockets.get(i).getOutputs());
        }
        return results;
    }

    /**
     * Checks if the given jar file exists, and that the given main class is in that file.
     *
     * Throws an error if either condition fails.
     *
     * @param workerJar path (can be relative) of the jar file
     * @param mainClass main class to look for, in package notation as given as a jvm argument.
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static void checkJar(String workerJar, String mainClass) throws ClassNotFoundException, IOException {
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

    private int subset(int iterations, int numWorkers, int i) {
        return iterations/numWorkers + ((i < iterations%numWorkers)?1:0);
    }
}
