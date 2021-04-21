package parspice.job;

import parspice.ParSPICE;
import parspice.io.IOManager;
import parspice.io.IServer;
import parspice.sender.Sender;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Superclass of all Jobs that don't take input arguments sent from
 * the main process, and do return outputs.
 *
 * @param <I> The type given to the job by the main process.
 */
public abstract class IJob<I> extends Job<Void> {

    private final Sender<I> inputSender;

    private Socket inputSocket;
    private ObjectInputStream ois;

    /**
     * [main] inputs supplied by user
     */
    private List<I> inputs;

    public IJob(Sender<I> inputSender) {
        this.inputSender = inputSender;
    }

    /**
     * [main process] Initialize the job with the inputs it needs to run.
     *
     * @param numWorkers number of workers to use.
     * @param inputs inputs to split among the workers
     * @return this (builder pattern)
     */
    public final IJob<I> init(int numWorkers, List<I> inputs) {
        this.numWorkers = numWorkers;
        this.inputs = inputs;
        this.numTasks = inputs.size();

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
        if (inputs == null) {
            throw new IllegalStateException("Inputs must be specified.");
        }
        ArrayList<IOManager<?,?,Void>> ioManagers = new ArrayList<>(numWorkers);

        int task = 0;
        for (int i = 0; i < numWorkers; i++) {
            int taskSubset = taskSubset(numTasks, numWorkers, i);
            IServer<Void,I> iServer;
            List<I> inputsSublist;
            inputsSublist = inputs.subList(task, task+taskSubset);
            iServer = new IServer<>(inputSender, null, inputsSublist, null, par.getMinPort() + 2*i, i);
            ioManagers.add(new IOManager<>(iServer, null, i));
            task += taskSubset;
        }

        runCommon(par, ioManagers);
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
