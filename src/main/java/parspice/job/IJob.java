package parspice.job;

import parspice.ParSPICE;
import parspice.io.IOManager;
import parspice.io.IServer;
import parspice.sender.Sender;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static parspice.Worker.*;

/**
 * Superclass of all Worker tasks that don't take input arguments sent from
 * the main process, and do return outputs.
 *
 * @param <I> The type given to the worker by the main process.
 */
public abstract class IJob<I> extends Job<Void, I, Void> {

    private final Sender<I> inputSender;

    private Socket inputSocket;
    private ObjectInputStream ois;

    private List<I> inputs;

    public final IJob<I> init(int numWorkers, List<I> inputs) {
        this.numWorkers = numWorkers;
        this.inputs = inputs;
        this.numTasks = inputs.size();

        validate();

        return this;
    }

    public IJob(Sender<I> inputSender) {
        this.inputSender = inputSender;
    }

    @Override
    public final void setupWrapper() throws Exception {
        setup();
    }
    /**
     * Prepares the argument input stream and repeatedly calls task.
     */
    @Override
    public final void taskWrapper() throws Exception {
        for (int i = getStartIndex(); i < getStartIndex() + getTaskSubset(); i++) {
            task(inputSender.read(ois));
        }
    }

    @Override
    public final void startConnections() throws Exception {
        inputSocket = new Socket("localhost", getInputPort());
        ois = new ObjectInputStream(inputSocket.getInputStream());
    }

    @Override
    public final void endConnections() throws Exception {
        ois.close();
        inputSocket.close();
    }

    public final void run(ParSPICE par) throws Exception {
        if (inputs == null) {
            throw new IllegalStateException("Inputs must be specified.");
        }
        ArrayList<IOManager<Void,I,Void>> ioManagers = new ArrayList<>(numWorkers);

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

    public void setup() throws Exception {}

    /**
     * Called repeatedly, once for each input sent from the main process.
     *
     * @param input The input given by the main process to the worker.
     * @throws Exception
     */
    public abstract void task(I input) throws Exception;
}
