package parspice.job;

import parspice.ParSPICE;
import parspice.io.IOManager;
import parspice.io.IServer;
import parspice.io.OServer;
import parspice.sender.Sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Superclass of all jobs that take inputs to the setup function,
 * but don't take task inputs or return task outputs.
 *
 * @param <S> The type given to the setup function by the main process.
 */
public abstract class SJob<S> extends Job<Void> {

    private final Sender<S> setupSender;

    private Socket inputSocket;
    private ObjectInputStream ois;

    /**
     * [main] setup inputs supplied by the user
     */
    private List<S> setupInputs;

    public SJob(Sender<S> setupSender) {
        this.setupSender = setupSender;
    }

    /**
     * [main process] Initialize the job with the inputs it needs to run, including a
     * single input to be copied to the argument of each job's setup function.
     *
     * @param numWorkers number of workers to use.
     * @param numTasks number of tasks to run.
     * @param setupInput setup input to give to each job's setup function.
     * @return this (builder pattern)
     */
    public final SJob<S> init(int numWorkers, int numTasks, S setupInput) {
        this.numWorkers = numWorkers;
        this.numTasks = numTasks;
        this.setupInputs = new ArrayList<S>(numWorkers);
        for (int i = 0; i < numWorkers; i++) {
            this.setupInputs.add(setupInput);
        }

        validate();

        return this;
    }

    /**
     * [main process] initialize the job with the inputs it needs to run, including a list
     * of setup inputs, where one will be given to each job's setup function.
     *
     * @param numTasks number of tasks to run.
     * @param setupInputs list of setup inputs to give to the jobs.
     * @return this (builder pattern)
     */
    public final SJob<S> init(int numTasks, List<S> setupInputs) {
        this.numWorkers = setupInputs.size();
        this.numTasks = numTasks;
        this.setupInputs = setupInputs;

        validate();

        return this;
    }

    /**
     * [main process] Runs the job in parallel.
     *
     * @param par a ParSPICE instance with worker jar and minimum port number.
     * @throws Exception
     */
    public final void run(ParSPICE par) throws Exception {
        if (setupInputs == null) {
            throw new IllegalStateException("Setup input(s) must be specified");
        }
        if (setupInputs.size() != numWorkers) {
            throw new IllegalStateException("Don't specify numWorkers when job.setupInputs() is used. The number of workers will be inferred.");
        }

        ArrayList<IOManager<?, ?, Void>> ioManagers = new ArrayList<>(numWorkers);

        int task = 0;
        int minPort = par.getMinPort();

        for (int i = 0; i < numWorkers; i++) {
            int taskSubset = taskSubset(numTasks, numWorkers, i);
            IServer<S, Void> iServer = new IServer<>(null, setupSender, null, setupInputs.get(i), minPort + 2 * i, i);
            ioManagers.add(new IOManager<>(iServer, null, i));
            task += taskSubset;
        }

        runCommon(par, ioManagers);
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
            task(i);
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
     * should just use an AutoJob).
     *
     * @throws Exception any exception the user code needs to throw
     */
    public abstract void setup(S input) throws Exception;

    /**
     * Called repeatedly, once for each integer {@code i} in the index range
     * given by the command line arguments. The user must implement this function.
     *
     * @param i The integer index of the task. It should be used to calculate
     *          the initial state or values needed by the task.
     *          The task receives no other indication of which iteration it is.
     * @throws Exception any exception the user code needs to throw
     */
    public abstract void task(int i) throws Exception;
}
