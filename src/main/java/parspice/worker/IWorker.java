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

    public IWorker(Sender<I> inputSender) {
        this.inputSender = inputSender;
    }

    /**
     * Prepares the argument input stream and repeatedly calls task.
     */
    public final void run() throws Exception {
        Socket inputSocket = new Socket("localhost", inputPort);
        ObjectInputStream ois = new ObjectInputStream(inputSocket.getInputStream());

        for (int i = startIndex; i < startIndex + taskSubset; i++) {
            task(inputSender.read(ois));
        }
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
