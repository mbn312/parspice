package parspice.worker;

import parspice.sender.Sender;

import java.io.*;
import java.net.Socket;

/**
 * Superclass of all Worker tasks that don't take input arguments sent from
 * the main process, and do return outputs.
 *
 * @param <I> The type given to the worker by the main process.
 */
public abstract class IWorker<I> extends Worker {

    private final Sender<I> inputSender;

    private Socket inputSocket;
    private ObjectInputStream ois;

    public IWorker(Sender<I> inputSender) {
        this.inputSender = inputSender;
    }

    /**
     * Prepares the argument input stream and repeatedly calls task.
     */
    public final void run() throws Exception {
        for (int i = startIndex; i < startIndex + taskSubset; i++) {
            task(inputSender.read(ois));
        }
    }

    @Override
    public final void startConnections() throws Exception {
        inputSocket = new Socket("localhost", inputPort);
        ois = new ObjectInputStream(inputSocket.getInputStream());
    }

    @Override
    public final void endConnections() throws Exception {
        ois.close();
        inputSocket.close();
    }

    /**
     * Get an instance of the input sender.
     *
     * @return instance of the input sender.
     */
    public Sender<I> getInputSender() {
        return inputSender;
    }

    /**
     * Called repeatedly, once for each input sent from the main process.
     *
     * @param input The input given by the main process to the worker.
     * @throws Exception
     */
    public abstract void task(I input) throws Exception;
}
