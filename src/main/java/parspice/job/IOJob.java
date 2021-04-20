package parspice.job;

import parspice.ParSPICE;
import parspice.io.IOManager;
import parspice.io.IServer;
import parspice.io.OServer;
import parspice.sender.Sender;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static parspice.Worker.*;

/**
 * Superclass of all Worker tasks that don't take input arguments sent from
 * the main process, and do return outputs. All subclasses should include a main entry point that
 * calls {@code run(new This(), args) with an instance of themselves.
 *
 * @param <I> The type given by the main process as argument.
 * @param <O> The type returned by the worker to the main process.
 */
public abstract class IOJob<I,O> extends Job<Void, I, O> {

    private final Sender<I> inputSender;
    private final Sender<O> outputSender;

    private Socket inputSocket;
    private Socket outputSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private List<I> inputs;
    private ArrayList<O> outputs;

    public final IOJob<I,O> init(int numWorkers, List<I> inputs) {
        this.numWorkers = numWorkers;
        this.inputs = inputs;
        this.numTasks = inputs.size();

        validate();

        return this;
    }

    public IOJob(Sender<I> inputSender, Sender<O> outputSender) {
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
        for (int i = getStartIndex(); i < getStartIndex() + getTaskSubset(); i++) {
            outputSender.write(task(inputSender.read(ois)), oos);
        }
    }

    @Override
    public final void startConnections() throws Exception {
        inputSocket = new Socket("localhost", getInputPort());
        outputSocket = new Socket("localhost", getOutputPort());
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

    public final ArrayList<O> run(ParSPICE par) throws Exception {
        if (inputs == null) {
            throw new IllegalStateException("Inputs must be specified.");
        }
        ArrayList<IOManager<Void,I,O>> ioManagers = new ArrayList<>(numWorkers);

        int task = 0;
        int minPort = par.getMinPort();

        for (int i = 0; i < numWorkers; i++) {
            int taskSubset = taskSubset(numTasks, numWorkers, i);
            List<I> inputsSublist = inputs.subList(task, task+taskSubset);
            IServer<Void,I> iServer = new IServer<>(inputSender, null, inputsSublist, null, minPort + 2*i, i);
            OServer<O> oServer = new OServer<>(outputSender, taskSubset, minPort + 2*i + 1, i);
            ioManagers.add(new IOManager<>(iServer, oServer, i));
            task += taskSubset;
        }

        runCommon(par, ioManagers);

        return aggregateOutputs(ioManagers);
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
