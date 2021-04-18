package parspice.worker;

import parspice.Job;
import parspice.sender.Sender;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Superclass of all Worker tasks that don't take input arguments sent from
 * the main process, and do return outputs. All subclasses should include a main entry point that
 * calls {@code run(new This(), args) with an instance of themselves.
 *
 * @param <S> The type given to the setup function by the main process.
 * @param <I> The type given to the task function by the main process.
 * @param <O> The type returned by the worker to the main process.
 */
public abstract class SIOWorker<S,I,O> extends Worker<S,I,O> {

    private final Sender<S> setupSender;
    private final Sender<I> inputSender;
    private final Sender<O> outputSender;

    private Socket inputSocket;
    private Socket outputSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public SIOWorker(Sender<S> setupSender, Sender<I> inputSender, Sender<O> outputSender) {
        this.setupSender = setupSender;
        this.inputSender = inputSender;
        this.outputSender = outputSender;
    }

    @Override
    public final void setupWrapper() throws Exception {
        setup(setupSender.read(ois));
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
    public final Job<S,I,O> job() {
        return new Job<>(this);
    }

    @Override
    public final Sender<S> getSetupInputSender() {
        return setupSender;
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


    public abstract void setup(S input) throws Exception;

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
