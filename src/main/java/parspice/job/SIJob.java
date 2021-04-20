package parspice.job;

import parspice.ParSPICE;
import parspice.io.IOManager;
import parspice.io.IServer;
import parspice.io.OServer;
import parspice.sender.Sender;

import java.io.ObjectInputStream;
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
public abstract class SIJob<S,I> extends Job<S,I,Void> {

    private final Sender<S> setupSender;
    private final Sender<I> inputSender;

    private Socket inputSocket;
    private ObjectInputStream ois;

    private List<S> setupInputs;
    private S setupInput;
    private List<I> inputs;

    public final SIJob<S,I> init(int numWorkers, S setupInput, List<I> inputs) {
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
    public final SIJob<S,I> init(List<S> setupInputs, List<I> inputs) {
        this.numWorkers = setupInputs.size();
        this.setupInputs = setupInputs;
        this.inputs = inputs;
        this.numTasks = inputs.size();

        validate();

        return this;
    }

    public SIJob(Sender<S> setupSender, Sender<I> inputSender) {
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

        ArrayList<IOManager<S,I,Void>> ioManagers = new ArrayList<>(numWorkers);

        int task = 0;
        int minPort = par.getMinPort();

        for (int i = 0; i < numWorkers; i++) {
            int taskSubset = taskSubset(numTasks, numWorkers, i);
            List<I> inputsSublist = inputs.subList(task, task+taskSubset);
            IServer<S,I> iServer = new IServer<>(inputSender, setupSender, inputsSublist, localSetupInputs.get(i), minPort + 2*i, i);
            ioManagers.add(new IOManager<>(iServer, null, i));
            task += taskSubset;
        }

        runCommon(par, ioManagers);
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
