package parspice;

import parspice.io.IOManager;
import parspice.io.IServer;
import parspice.io.OServer;
import parspice.sender.Sender;
import parspice.worker.AutoWorker;
import parspice.worker.Worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Job<S, I, O> {
    public final Worker<S,I,O> worker;
    public List<I> inputs = null;
    public List<S> setupInputs = null;
    public S setupInput = null;
    public int numTasks = -1;

    private ArrayList<O> outputs = null;

    public Job(Worker<S,I,O> worker) {
        this.worker = worker;
    }

    public Job<S,I,O> inputs(List<I> inputs) {
        this.inputs = inputs;
        this.numTasks = inputs.size();
        return this;
    }

    public Job<S,I,O> setupInputs(List<S> setupInputs) {
        this.setupInputs = setupInputs;
        return this;
    }

    public Job<S,I,O> setupInput(S setupInput) {
        this.setupInput = setupInput;
        return this;
    }

    public Job<S,I,O> numTasks(int numTasks) {
        this.numTasks = numTasks;
        return this;
    }

    void setOutputs(ArrayList<O> outputs) {
        this.outputs = outputs;
    }

    public ArrayList<O> getOutputs() throws NullPointerException {
        if (outputs == null) {
            throw new NullPointerException("Worker " + worker.getClass().getName() + " does not produce output.");
        }
        return outputs;
    }

    public ArrayList<IOManager<S,I,O>> getIOManagers(int numWorkers, int minPort) throws IOException {
        ArrayList<IOManager<S,I,O>> ioManagers = new ArrayList<>(numWorkers);
        int task = 0;

        Sender<S> setupInputSender = worker.getSetupInputSender();
        Sender<I> inputSender = worker.getInputSender();
        Sender<O> outputSender = worker.getOutputSender();

        for (int i = 0; i < numWorkers; i++) {
            int taskSubset = ParSPICE.taskSubset(numTasks, numWorkers, i);
            IServer<S,I> iServer = null;
            OServer<O> oServer = null;
            if (setupInputSender != null || inputSender != null) {
                List<I> inputsSublist = null;
                S setupInputLocal = setupInput;
                if (inputSender != null) {
                    inputsSublist = inputs.subList(task, task+taskSubset);
                }
                if (setupInputs != null) {
                    if (setupInputs.size() != numWorkers) {
                        throw new IOException("Setup inputs list size must equal number of workers. Was "
                                + setupInputs.size() + ", expected " + numWorkers + ".");
                    }
                    setupInputLocal = setupInputs.get(i);
                }
                iServer = new IServer<>(inputSender, setupInputSender, inputsSublist, setupInputLocal, minPort + 2*i, i);
            }
            if (outputSender != null) {
                oServer = new OServer<>(outputSender, taskSubset, minPort + 2*i + 1, i);
            }
            ioManagers.add(new IOManager<>(iServer, oServer, i));
            task += taskSubset;
        }
        return ioManagers;
    }
}
