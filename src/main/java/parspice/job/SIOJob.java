package parspice.job;

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
public abstract class SIOJob<S,I,O> extends Job<O> {

    private final Sender<S> setupSender;
    private final Sender<I> inputSender;
    private final Sender<O> outputSender;

    private Socket inputSocket;
    private Socket outputSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    /**
     * [main] setup inputs supplied by the user
     */
    private List<S> setupInputs;
    /**
     * [main] inputs supplied by the user
     */
    private List<I> inputs;

    public SIOJob(Sender<S> setupSender, Sender<I> inputSender, Sender<O> outputSender) {
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
    public final SIOJob<S,I,O> init(int numWorkers, S setupInput, List<I> inputs) {
        this.numWorkers = numWorkers;
        this.setupInputs = new ArrayList<S>(numWorkers);
        for (int i = 0; i < numWorkers; i++) {
            this.setupInputs.add(setupInput);
        }
        this.inputs = inputs;
        this.numTasks = inputs.size();

        validate();

        return this;
    }

    /**
     * [main process] initialize the job with the inputs it needs to run, including a list
     * of setup inputs, where one will be given to each job's setup function.
     *
     * @param setupInputs list of setup inputs to give to the jobs.
     * @param inputs list of inputs to split among the workers
     * @return this (builder pattern)
     */
    public final SIOJob<S,I,O> init(List<S> setupInputs, List<I> inputs) {
        this.numWorkers = setupInputs.size();
        this.setupInputs = setupInputs;
        this.inputs = inputs;
        this.numTasks = inputs.size();

        validate();

        return this;
    }

    /**
     * [main process] Runs the job in parallel.
     *
     * @param par a ParSPICE instance with worker jar and minimum port number.
     * @return An ArrayList of outputs, collected from the job's return values.
     * @throws Exception
     */
    public final ArrayList<O> run(ParSPICE par) throws Exception {
        if (inputs == null) {
            throw new IllegalStateException("Inputs must be specified.");
        }
        if (setupInputs == null) {
            throw new IllegalStateException("Setup input(s) must be specified");
        }
        if (setupInputs.size() != numWorkers) {
            throw new IllegalStateException("Don't specify numWorkers when job.setupInputs() is used. The number of workers will be inferred.");
        }

        ArrayList<IOManager<?,?,O>> ioManagers = new ArrayList<>(numWorkers);

        int task = 0;
        int minPort = par.getMinPort();

        for (int i = 0; i < numWorkers; i++) {
            int taskSubset = taskSubset(numTasks, numWorkers, i);
            List<I> inputsSublist = inputs.subList(task, task+taskSubset);
            IServer<S,I> iServer = new IServer<>(inputSender, setupSender, inputsSublist, setupInputs.get(i), minPort + 2*i, i);
            OServer<O> oServer = new OServer<>(outputSender, taskSubset, minPort + 2*i + 1, i);
            ioManagers.add(new IOManager<>(iServer, oServer, i));
            task += taskSubset;
        }

        runCommon(par, ioManagers);

        return collectOutputs(ioManagers);
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
