package parspice.worker;

import parspice.sender.Sender;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Superclass of all Workers that don't take input arguments sent from
 * the main process, and do return outputs.
 *
 * @param <I> The type given to the job by the main process.
 */
public abstract class IWorker<I> extends Worker {

    private final Sender<I> inputSender;

    private Socket inputSocket;
    private ObjectInputStream ois;

    public IWorker(Sender<I> inputSender) {
        this.inputSender = inputSender;
    }

    /**
     * [main process] Initialize the job with the inputs it needs to run.
     *
     * @param numWorkers number of workers to use.
     * @param inputs inputs to split among the worker
     * @return an initialized Job, ready to run
     */
    public final VoidJob<Void,I> init(int numWorkers, List<I> inputs) {
        VoidJob<Void,I> job = new VoidJob<>(this);

        job.numWorkers = numWorkers;
        job.inputs = inputs;
        job.numTasks = inputs.size();
        job.inputSender = inputSender;

        job.validate();

        return job;
    }

    /**
     * [worker process] Calls setup.
     *
     * The user cannot call or override this function.
     *
     * @throws Exception any exception the user code needs to throw
     */
    @Override
    final void setupWrapper() throws Exception {
        setup();
    }

    /**
     * [worker process] Repeatedly reads an input from the stream and calls task.
     *
     * The user cannot call or override this function.
     *
     * @throws Exception any exception the user code needs to throw
     */
    @Override
    final void taskWrapper() throws Exception {
        for (int i = getStartIndex(); i < getStartIndex() + getTaskSubset(); i++) {
            task(inputSender.read(ois));
        }
    }

    /**
     * [worker process] Starts the input socket connection with the main process.
     *
     * @throws IOException if connection cannot be made
     */
    @Override
    final void startConnections() throws IOException {
        inputSocket = new Socket("localhost", getInputPort());
        ois = new ObjectInputStream(inputSocket.getInputStream());
    }

    /**
     * [worker process] Ends the input socket connection with the main process.
     *
     * @throws IOException if the connection cannot be ended.
     */
    @Override
    final void endConnections() throws IOException {
        ois.close();
        inputSocket.close();
    }

    /**
     * [worker] Called once on each worker when the job starts running.
     *
     * The user can optionally override this function; by default it does nothing.
     *
     * @throws Exception any exception the user code needs to throw
     */
    public void setup() throws Exception {}

    /**
     * [worker] Called repeatedly, once for each input sent from the main process.
     *
     * @param input The input given by the main process to the worker.
     * @throws Exception any exception the user code needs to throw
     */
    public abstract void task(I input) throws Exception;
}
