package parspice.worker;

import parspice.Job;
import parspice.ParSPICE;
import parspice.io.IOManager;
import parspice.io.IServer;
import parspice.io.OServer;
import parspice.sender.Sender;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Superclass of all Worker tasks that don't take input arguments sent from
 * the main process, and do return outputs.
 *
 * @param <I> The type given to the worker by the main process.
 */
public abstract class IWorker<I> extends Worker<Void, I, Void> {

    private final Sender<I> inputSender;

    private Socket inputSocket;
    private ObjectInputStream ois;

    public IWorker(Sender<I> inputSender) {
        this.inputSender = inputSender;
    }

    @Override
    public final void setupWrapper() throws Exception {
        setup();
    }
    /**
     * Prepares the argument input stream and repeatedly calls task.
     */
    @Override
    public final void taskWrapper() throws Exception {
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

    @Override
    public final Job<Void,I,Void> job() {
        return new Job<>(this);
    }
    /**
     * Get an instance of the input sender.
     *
     * @return instance of the input sender.
     */
    @Override
    public final Sender<I> getInputSender() {
        return inputSender;
    }

    @Override
    public final Sender<Void> getSetupInputSender() {
        return null;
    }

    @Override
    public final Sender<Void> getOutputSender() {
        return null;
    }

    public void setup() throws Exception {}

    /**
     * Called repeatedly, once for each input sent from the main process.
     *
     * @param input The input given by the main process to the worker.
     * @throws Exception
     */
    public abstract void task(I input) throws Exception;
}
