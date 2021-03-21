package parspice;

import java.net.ServerSocket;
import java.util.List;
import java.util.ArrayList;

public class Dispatcher {
    @SuppressWarnings("unchecked")
    public static <T> List<T> run(String workerJar, Returner<T> sender, int iterations, int numWorkers) throws Exception {
        List<T> results = new ArrayList<>(iterations);
        Process[] processes = new Process[numWorkers];
        SocketListener<T>[] sockets = new SocketListener[numWorkers];
        int iteration = 0;
        for (int i = 0; i < numWorkers; i++) {
            int subset = iterations/numWorkers + ((i < iterations%numWorkers)?1:0);
            String args = workerJar + " " + (50050 + i) + " " + iteration + " " + subset;
            System.out.println(args);
            sockets[i] = new SocketListener<>(
                sender,
                new ServerSocket(50050 + i),
                i,
                subset
            );
            sockets[i].start();
            processes[i] = Runtime.getRuntime().exec("java -jar "
                + args);
            iteration += subset;
        }
        for (int i = 0; i < numWorkers; i++) {
            sockets[i].join();
            results.addAll(sockets[i].getResults());
        }
        return results;
    }
}
