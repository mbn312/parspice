package parspice;

import parspice.io.IOManager;
import parspice.job.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
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

    public int getMinPort() {
        return minPort;
    }
    public String getWorkerJar() {
        return workerJar;
    }

    /**
     * Creates a new ParSPICE object for a given jar file.
     *
     * @param workerJar the jar for all tasks on this instance to be
     *                  run from.
     */
    public ParSPICE(String workerJar, int minPort) throws IOException, ClassNotFoundException {
        this.workerJar = workerJar;
        this.minPort = minPort;

        checkJar();
    }

    /**
     * Checks that the given file exists, and that it is a .jar file. Throws an exception if not.
     *
     * @throws IOException
     */
    private void checkJar() throws ClassNotFoundException, IOException {
        String extension = workerJar.substring(workerJar.length()-4);
        if (!extension.equals(".jar")) {
            throw new IOException("workerJar must be a .jar file, not: " + extension);
        }
        File file = new File(workerJar);
        if (!file.exists()) {
            throw new FileNotFoundException(workerJar);
        }
        checkClass("parspice.job.Job");
    }

    /**
     * Checks that the given class is in the the jar file. Throws an exception if not.
     *
     * @param cls class to look for, in package notation as given as a jvm argument.
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void checkClass(String cls) throws ClassNotFoundException, IOException {
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





}