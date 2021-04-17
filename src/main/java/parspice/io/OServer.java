package parspice.io;

import parspice.sender.Sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * A server for receiving outputs from a worker.
 *
 * @param <O> the type of outputs from the worker.
 */
public class OServer<O> implements Runnable {

    private final ServerSocket serverSocket;
    private final Sender<O> outputSender;
    private final int workerID;
    private final int taskSubset;

    private final ArrayList<O> outputs;

    public OServer(Sender<O> outputSender, int taskSubset, int port, int workerID) throws IOException {
        this.serverSocket = new ServerSocket(port);

        this.outputSender = outputSender;
        this.outputs = new ArrayList<>(taskSubset);
        this.workerID = workerID;
        this.taskSubset = taskSubset;
    }

    /**
     * Gets a connection to the worker, and reads all outputs into the outputs list.
     */
    @Override
    public void run() {
        try {
            Socket socket = serverSocket.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            for (int i = 0; i < taskSubset; i++) {
                outputs.add(outputSender.read(ois));
            }
            ois.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.err.println(
                    "OServer thread " + workerID + " failed. Check log file 'ParSPICE_worker_log_"
                    + workerID
                    + ".txt for details. If it does not exist, the worker wasn't able to run."
            );
            System.err.println(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Return the received outputs. Should only be called after run has completed.
     *
     * @return the outputs from the worker
     */
    public ArrayList<O> getOutputs() {
        return outputs;
    }
}
