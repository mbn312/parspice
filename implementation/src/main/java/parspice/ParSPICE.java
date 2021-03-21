package parspice;

import parspice.sender.Sender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ParSPICE {
    public static <T> List<T> run(String workerJar, String mainClass, Sender<T> sender, int iterations, int numWorkers) throws Exception {
        checkJar(workerJar, mainClass);

        List<T> results = new ArrayList<>(iterations);
        Process[] processes = new Process[numWorkers];
        List<SocketListener<T>> sockets = new ArrayList<>(numWorkers);
        int iteration = 0;
        for (int i = 0; i < numWorkers; i++) {
            int subset = iterations/numWorkers + ((i < iterations%numWorkers)?1:0);
            String args = workerJar + " " + mainClass + " " + (50050 + i) + " " + iteration + " " + subset;
            sockets.add(new SocketListener<>(
                    sender,
                    new ServerSocket(50050 + i),
                    i,
                    subset
            ));
            sockets.get(i).start();
            processes[i] = Runtime.getRuntime().exec("java -cp "
                    + args);
            iteration += subset;
        }
        for (int i = 0; i < numWorkers; i++) {
            sockets.get(i).join();
            processes[i].waitFor();
            results.addAll(sockets.get(i).getResults());
        }
        return results;
    }

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
}
