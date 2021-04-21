package parspice.worker;

import parspice.sender.Sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Superclass of all jobs that take inputs to both the setup and task functions,
 * but don't return outputs.
 *
 * @param <S> The type given to the setup function by the main process.
 * @param <I> The type given to the task function by the main process.
 */
public abstract class SIWorker<S,I> extends Worker<Void> {

    private final Sender<S> setupSender;
    private final Sender<I> inputSender;

    private Socket inputSocket;
    private ObjectInputStream ois;

    public SIWorker(Sender<S> setupSender, Sender<I> inputSender) {
        this.setupSender = setupSender;
        this.inputSender = inputSender;
    }

    /**
     * [main process] Initialize the job with the inputs it needs to run, including a
     * single input to be copied to the argument of each job's setup function.
     *
     * @param numWorkers number of workers to use.
     * @param setupInput setup input to give to each job's setup function.
     * @param inputs inputs to split among the workers
     * @return this (builder pattern)
     */
    public final VoidJob<S,I> init(int numWorkers, S setupInput, List<I> inputs) {
        VoidJob<S,I> job = new VoidJob<>(this);

        job.numWorkers = numWorkers;
        job.setupInputs = new ArrayList<S>(numWorkers);
        for (int i = 0; i < numWorkers; i++) {
            job.setupInputs.add(setupInput);
        }
        job.inputs = inputs;
        job.numTasks = inputs.size();
        job.setupSender = setupSender;
        job.inputSender = inputSender;

        job.validate();

        return job;
    }

    /**
     * [main process] initialize the job with the inputs it needs to run, including a list
     * of setup inputs, where one will be given to each job's setup function.
     *
     * @param setupInputs list of setup inputs to give to the jobs.
     * @param inputs list of inputs to split among the workers
     * @return this (builder pattern)
     */
    public final VoidJob<S,I> init(List<S> setupInputs, List<I> inputs) {
        VoidJob<S,I> job = new VoidJob<>(this);

        job.numWorkers = setupInputs.size();
        job.setupInputs = setupInputs;
        job.inputs = inputs;
        job.numTasks = inputs.size();
        job.setupSender = setupSender;
        job.inputSender = inputSender;

        job.validate();

        return job;
    }

    /**
     * [worker process] Reads a setup input and calls setup.
     *
     * The user cannot call or override this function.
     *
     * @throws Exception any exception the user code needs to throw
     */
    @Override
    final void setupWrapper() throws Exception {
        setup(setupSender.read(ois));
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
     * The user must override this function (if it has no behavior, the user
     * should just use an IJob).
     *
     * @param input the input given by the main process
     * @throws Exception any exception the user code needs to throw
     */
    public abstract void setup(S input) throws Exception;

    /**
     * Called repeatedly, once for each integer {@code i} in the index range
     * given by the command line arguments.
     *
     * @param input The input given by the main process to the worker.
     * @throws Exception any exception the user code needs to throw
     */
    public abstract void task(I input) throws Exception;
}
