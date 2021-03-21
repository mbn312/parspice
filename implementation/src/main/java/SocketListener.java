import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class SocketListener<T> implements Runnable {
    private Thread thread;
    private final int batchSize;
    private final int workerIndex;
    private final ServerSocket serverSocket;
    private final Returner<T> sender;

    private final List<T> results;

    public SocketListener(Returner<T> send, ServerSocket s, int wi, int bs) {
        serverSocket = s;
        workerIndex = wi;
        batchSize = bs;
        results = new ArrayList<>(bs);
        sender = send;
    }

    @Override
    public void run() {
        try {
            InputStream is = serverSocket.accept().getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            for (int i = 0; i < batchSize; i++) {
                results.add(sender.read(ois));
            }
            ois.close();
            is.close();
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Socket Listener " + workerIndex + " failed after " + results.size());
            e.printStackTrace();
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this, "socket_listener_" + Integer.toString(workerIndex));
            thread.start();
        }
    }

    public void join() throws InterruptedException {
        thread.join();
    }

    public List<T> getResults() {
        return results;
    }
}
