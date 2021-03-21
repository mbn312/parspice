package parspice.socketManager;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class SocketManager<O> implements Runnable {
    private Thread thread;

    protected final List<O> outputs;
    protected final int workerIndex;
    protected final int batchSize;

    private final ServerSocket serverSocket;
    private Socket socket;

    public SocketManager(ServerSocket serverSocket, int workerIndex, int batchSize) {
        outputs = new ArrayList<>(batchSize);
        this.serverSocket = serverSocket;
        this.workerIndex = workerIndex;
        this.batchSize = batchSize;
    }

    @Override
    public void run() {
        try {
            socket = serverSocket.accept();
            sendAndReceive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void sendAndReceive();

    public void start() {
        if (thread == null) {
            thread = new Thread(this, "socket_manager" + Integer.toString(workerIndex));
            thread.start();
        }
    }

    public void join() throws Exception {
        thread.join();
        socket.close();
        serverSocket.close();
    }

    protected ObjectInputStream getInputStream() throws IOException {
        return new ObjectInputStream(socket.getInputStream());
    }

    protected ObjectOutputStream getOutputStream() throws IOException {
        return new ObjectOutputStream(socket.getOutputStream());
    }

    public List<O> getOutputs() {
        return outputs;
    }
}
