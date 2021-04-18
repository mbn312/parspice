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
public abstract class SIWorker<S,I> extends Worker<S,I,Void> {

    private final Sender<S> setupSender;
    private final Sender<I> inputSender;

    private Socket inputSocket;
    private ObjectInputStream ois;

    public SIWorker(Sender<S> setupSender, Sender<I> inputSender) {
        this.setupSender = setupSender;
        this.inputSender = inputSender;
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
    public final Job<S,I,Void> job() {
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
    public final Sender<Void> getOutputSender() {
        return null;
    }


    public abstract void setup(S input) throws Exception;

    /**
     * Called repeatedly, once for each integer {@code i} in the index range
     * given by the command line arguments.
     *
     * @param input The input given by the main process to the worker.
     * @throws Exception
     */
    public abstract void task(I input) throws Exception;
}
