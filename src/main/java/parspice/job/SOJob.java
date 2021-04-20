package parspice.job;

import parspice.ParSPICE;
import parspice.io.IOManager;
import parspice.io.IServer;
import parspice.io.OServer;
import parspice.sender.Sender;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static parspice.Worker.*;

/**
 * Superclass of all Worker tasks that don't take input arguments sent from
 * the main process, and do return outputs. All subclasses should include a main entry point that
 * calls {@code run(new This(), args) with an instance of themselves.
 *
 * @param <S> The type given to the setup function by the main process.
 * @param <I> The type given to the task function by the main process.
 * @param <O> The type returned by the worker to the main process.
 */
public abstract class SOJob<S,O> extends Job<S,Void,O> {

    private final Sender<S> setupSender;
    private final Sender<O> outputSender;

    private Socket inputSocket;
    private Socket outputSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private List<S> setupInputs;
    private S setupInput;

    public final SOJob<S,O> init(int numWorkers, int numTasks, S setupInput) {
        this.numWorkers = numWorkers;
        this.numTasks = numTasks;
        this.setupInputs = new ArrayList<S>(numWorkers);
        for (int i = 0; i < numWorkers; i++) {
            this.setupInputs.add(setupInput);
        }

        validate();

        return this;
    }
    public final SOJob<S,O> init(int numTasks, List<S> setupInputs) {
        this.numWorkers = setupInputs.size();
        this.numTasks = numTasks;
        this.setupInputs = setupInputs;

        validate();

        return this;
    }

    public SOJob(Sender<S> setupSender, Sender<O> outputSender) {
        this.setupSender = setupSender;
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
        for (int i = getStartIndex(); i < getStartIndex() + getTaskSubset(); i++) {
            outputSender.write(task(i), oos);
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
        if (setupInputs == null && setupInput == null) {
            throw new IllegalStateException("Setup input(s) must be specified");
        }
        List<S> localSetupInputs;
        if (setupInputs != null) {
            if (setupInputs.size() != numWorkers) {
                throw new IllegalStateException("Don't specify numWorkers when job.setupInputs() is used. The number of workers will be inferred.");
            }
            localSetupInputs = setupInputs;
        } else {
            localSetupInputs = new ArrayList<>(numWorkers);
            for (int i = 0; i < numWorkers; i++) {
                localSetupInputs.add(setupInput);
            }
        }

        ArrayList<IOManager<S,Void,O>> ioManagers = new ArrayList<>(numWorkers);

        int task = 0;
        int minPort = par.getMinPort();

        for (int i = 0; i < numWorkers; i++) {
            int taskSubset = taskSubset(numTasks, numWorkers, i);
            IServer<S,Void> iServer = new IServer<>(null, setupSender, null, localSetupInputs.get(i), minPort + 2*i, i);
            OServer<O> oServer = new OServer<>(outputSender, taskSubset, minPort + 2*i + 1, i);
            ioManagers.add(new IOManager<>(iServer, oServer, i));
            task += taskSubset;
        }

        runCommon(par, ioManagers);

        return aggregateOutputs(ioManagers);
    }

    public abstract void setup(S input) throws Exception;

    /**
     * Called repeatedly, once for each integer {@code i} in the index range
     * given by the command line arguments.
     *
     * @return The value to be sent back to the main process.
     * @throws Exception
     */
    public abstract O task(int i) throws Exception;
}
