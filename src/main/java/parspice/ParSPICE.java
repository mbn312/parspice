package parspice;

import parspice.sender.Sender;
import parspice.io.IServer;
import parspice.io.OServer;
import parspice.io.IOManager;
import parspice.worker.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
     * Creates a new ParSPICE object for a given jar file.
     *
     * @param workerJar the jar for all tasks on this instance to be
     *                  run from.
     */
    public ParSPICE(String workerJar, int minPort) throws IOException {
        checkJar(workerJar);

        this.workerJar = workerJar;
        this.minPort = minPort;
    }

    public <S,I,O> Job<S,I,O> run(
            Job<S,I,O> job,
            int numWorkers
    ) throws Exception {
        String workerClass = job.worker.getClass().getName();
        checkClass(workerJar, "parspice.worker.Worker");
        checkClass(workerJar, workerClass);

        if (job.numTasks == -1) {
            throw new IllegalStateException("job.numTasks must be specified, or an inputs list must be given.");
        } else if (job.numTasks < 0) {
            throw new IllegalStateException("job.numTasks cannot be negative, was " + job.numTasks);
        }

        ArrayList<IOManager<S,I,O>> ioManagers = job.getIOManagers(numWorkers, minPort);

        if (ioManagers != null) {
            for (IOManager<S,I,O> manager : ioManagers) {
                manager.start();
            }
        }
        Process[] processes = new Process[numWorkers];
        int task = 0;
        for (int i = 0; i < numWorkers; i++) {
            int taskSubset = taskSubset(job.numTasks, numWorkers, i);
            String args = "-Dname=parspice_worker_" + i +
                    " -cp " + workerJar +
                    " parspice.worker.Worker" +
                    " " + workerClass +
                    " " + (minPort + 2*i) +
                    " " + task +
                    " " + taskSubset +
                    " " + i +
                    " " + numWorkers +
                    " " + job.numTasks;
            processes[i] = Runtime.getRuntime().exec("java " + args);
            task += taskSubset;
        }
        if (ioManagers != null) {
            for (IOManager<?,?,O> manager : ioManagers) {
                manager.join();
            }
        }
        for (int i = 0; i < numWorkers; i++) {
            processes[i].waitFor();
        }

        job.setOutputs(aggregateOutputs(ioManagers, job.numTasks));
        return job;
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
     * Checks that the given class is in the the jar file. Throws an exception if not.
     *
     * @param workerJar path (can be relative) to the jar file
     * @param cls class to look for, in package notation as given as a jvm argument.
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static void checkClass(String workerJar, String cls) throws ClassNotFoundException, IOException {
        JarFile jarFile = new JarFile(workerJar);
        Enumeration<JarEntry> e = jarFile.entries();
        while (e.hasMoreElements()) {
            JarEntry jarEntry = e.nextElement();
            if (jarEntry.getName().endsWith(".class")) {
                String className = jarEntry.getName()
                        .replace("/", ".")
                        .replace(".class", "");
                if (className.equals(cls)) {
                    return;
                }
            }
        }
        throw new ClassNotFoundException(cls);
    }

    /**
     * Calculate how many tasks should be given to a particular worker.
     *
     * Each worker is given an almost-equal taskSubset. If numTasks is not
     * an even multiple of numWorkers, the remainder is spread across the
     * first numTasks % numWorkers workers.
     *
     * @param numTasks total number of tasks
     * @param numWorkers number of workers
     * @param i the index of a particular worker
     * @return the number of tasks that worker should run
     */
    public static int taskSubset(int numTasks, int numWorkers, int i) {
        return numTasks/numWorkers + ((i < numTasks%numWorkers)?1:0);
    }

    private static <S,I,O> ArrayList<O> aggregateOutputs(ArrayList<IOManager<S,I,O>> ioManagers, int numTasks) {
        if (ioManagers != null) {
            ArrayList<O> results = ioManagers.get(0).getOutputs();
            if (results == null) {
                return null;
            }
            results.ensureCapacity(numTasks);
            for (IOManager<S, I, O> ioManager : ioManagers.subList(1, ioManagers.size())) {
                results.addAll(ioManager.getOutputs());
            }
            return results;
        } else {
            return null;
        }
    }
}
