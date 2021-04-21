package parspice.worker;

import parspice.ParSPICE;
import parspice.io.IOManager;
import parspice.io.IServer;
import parspice.io.OServer;
import parspice.sender.Sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Superclass of all Jobs that take inputs to both the setup and task functions,
 * and return output back to the main process.
 *
 * @param <S> The type given to the setup function by the main process.
 * @param <I> The type given to the task function by the main process.
 * @param <O> The type returned by the worker to the main process.
 */
public abstract class SIOWorker<S,I,O> extends Worker<O> {

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

    /**
     * [main process] Initialize the job with the inputs it needs to run, including a
     * single input to be copied to the argument of each job's setup function.
     *
     * @param numWorkers number of workers to use.
     * @param setupInput setup input to give to each job's setup function.
     * @param inputs inputs to split among the workers
     * @return this (builder pattern)
     */
    public final OJob<S,I,O> init(int numWorkers, S setupInput, List<I> inputs) {
        OJob<S,I,O> job = new OJob<>(this);

        job.numWorkers = numWorkers;
        job.setupInputs = new ArrayList<S>(numWorkers);
        for (int i = 0; i < numWorkers; i++) {
            job.setupInputs.add(setupInput);
        }
        job.inputs = inputs;
        job.numTasks = inputs.size();
        job.setupSender = setupSender;
        job.inputSender = inputSender;
        job.outputSender = outputSender;

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
    public final OJob<S,I,O> init(List<S> setupInputs, List<I> inputs) {
        OJob<S,I,O> job = new OJob<>(this);

        job.numWorkers = setupInputs.size();
        job.setupInputs = setupInputs;
        job.inputs = inputs;
        job.numTasks = inputs.size();
        job.setupSender = setupSender;
        job.inputSender = inputSender;
        job.outputSender = outputSender;

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
            outputSender.write(task(inputSender.read(ois)), oos);
        }
    }

    /**
     * [worker process] Starts the input and output socket connections with the main process.
     *
     * @throws IOException if the connections cannot be made
     */
    @Override
    final void startConnections() throws IOException {
        inputSocket = new Socket("localhost", getInputPort());
        outputSocket = new Socket("localhost", getOutputPort());
        ois = new ObjectInputStream(inputSocket.getInputStream());
        oos = new ObjectOutputStream(outputSocket.getOutputStream());
    }

    /**
     * [worker process] Ends the input and output connections with the main process.
     *
     * @throws IOException if the connections cannot be ended.
     */
    @Override
    final void endConnections() throws IOException {
        oos.close();
        outputSocket.close();
        ois.close();
        inputSocket.close();
    }

    /**
     * [worker] Called once on each worker when the job starts running.
     *
     * The user must override this function (if it has no behavior, the user
     * should just use an IOJob).
     *
     * @param input the input given by the main process
     * @throws Exception any exception the user code needs to throw
     */
    public abstract void setup(S input) throws Exception;

    /**
     * [worker] Called repeatedly, once for each input sent from the main process.
     *
     * @param input The input given by the main process to the worker.
     * @return The value to be sent back to the main process.
     * @throws Exception any exception the user code needs to throw
     */
    public abstract O task(I input) throws Exception;
}
