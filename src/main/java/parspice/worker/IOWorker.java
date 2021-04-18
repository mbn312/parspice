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
 * the main process, and do return outputs. All subclasses should include a main entry point that
 * calls {@code run(new This(), args) with an instance of themselves.
 *
 * @param <I> The type given by the main process as argument.
 * @param <O> The type returned by the worker to the main process.
 */
public abstract class IOWorker<I,O> extends Worker<Void, I, O> {

    private final Sender<I> inputSender;
    private final Sender<O> outputSender;

    private Socket inputSocket;
    private Socket outputSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public IOWorker(Sender<I> inputSender, Sender<O> outputSender) {
        this.inputSender = inputSender;
        this.outputSender = outputSender;
    }

    @Override
    public final void setupWrapper() throws Exception {
        setup();
    }

    /**
     * Prepares the input and output streams and repeatedly calls task.
     */
    public final void taskWrapper() throws Exception {
        for (int i = startIndex; i < startIndex + taskSubset; i++) {
            outputSender.write(task(inputSender.read(ois)), oos);
        }
    }

    @Override
    public final void startConnections() throws Exception {
        inputSocket = new Socket("localhost", inputPort);
        outputSocket = new Socket("localhost", inputPort + 1);
        ois = new ObjectInputStream(inputSocket.getInputStream());
        oos = new ObjectOutputStream(outputSocket.getOutputStream());
    }

    @Override
    public final void endConnections() throws Exception {
        oos.close();
        outputSocket.close();
        ois.close();
        inputSocket.close();
    }

    @Override
    public final Job<Void,I,O> job() {
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

    /**
     * Get an instance of the output sender.
     *
     * @return instance of the output sender.
     */
    @Override
    public final Sender<O> getOutputSender() {
        return outputSender;
    }

    @Override
    public final Sender<Void> getSetupInputSender() {
        return null;
    }

    public void setup() throws Exception {}
    /**
     * Called repeatedly, once for each integer {@code i} in the index range
     * given by the command line arguments.
     *
     * @param input The input given by the main process to the worker.
     * @return The value to be sent back to the main process.
     * @throws Exception
     */
    public abstract O task(I input) throws Exception;
}
