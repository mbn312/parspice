package parspice.io;

import parspice.sender.Sender;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Server for sending inputs to a worker.
 *
 * @param <I> The type of the arguments sent to the worker
 */
public class IServer<I> implements Runnable {

    private final ServerSocket serverSocket;
    private final Sender<I> inputSender;
    private final List<I> inputs;
    private final int workerID;

    public IServer(Sender<I> inputSender, List<I> inputs, int port, int workerID) throws IOException {
        this.serverSocket = new ServerSocket(port);

        this.inputSender = inputSender;
        this.inputs = inputs;
        this.workerID = workerID;
    }

    /**
     * Gets a connection to the worker and writes all the inputs
     */
    @Override
    public void run() {
        try {
            Socket socket = serverSocket.accept();
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            for (I input : inputs) {
                inputSender.write(input, oos);
            }
            oos.flush();
            oos.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.err.println(
                    "IServer thread " + workerID + " failed. Check log file 'ParSPICE_worker_log_"
                            + workerID
                            + ".txt for details. If it does not exist, the worker wasn't able to run."
            );
            System.err.println(e.toString());
            e.printStackTrace();
        }
    }
}
