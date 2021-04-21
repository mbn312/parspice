package parspice.worker;

import parspice.sender.Sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Superclass of all Workers that take inputs to the setup function but not the
 * task function, and return output from the task function.
 *
 * @param <S> The type given to the setup function by the main process.
 * @param <O> The type returned by the worker to the main process.
 */
public abstract class SOWorker<S,O> extends Worker<O> {

    private final Sender<S> setupSender;
    private final Sender<O> outputSender;

    private Socket inputSocket;
    private Socket outputSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public SOWorker(Sender<S> setupSender, Sender<O> outputSender) {
        this.setupSender = setupSender;
        this.outputSender = outputSender;
    }

    /**
     * [main process] Initialize the job with the inputs it needs to run, including a
     * single input to be copied to the argument of each job's setup function.
     *
     * @param numWorkers number of workers to use.
     * @param numTasks number of tasks to run.
     * @param setupInput setup input to give to each job's setup function.
     * @return an initialized Job, ready to run
     */
    public final OJob<S,Void,O> init(int numWorkers, int numTasks, S setupInput) {
        OJob<S,Void,O> job = new OJob<>(this);

        job.numWorkers = numWorkers;
        job.numTasks = numTasks;
        job.setupInputs = new ArrayList<S>(numWorkers);
        for (int i = 0; i < numWorkers; i++) {
            job.setupInputs.add(setupInput);
        }
        job.setupSender = setupSender;
        job.outputSender = outputSender;

        job.validate();

        return job;
    }

    /**
     * [main process] initialize the job with the inputs it needs to run, including a list
     * of setup inputs, where one will be given to each job's setup function.
     *
     * @param numTasks number of tasks to run.
     * @param setupInputs list of setup inputs to give to the jobs.
     * @return an initialized Job, ready to run
     */
    public final OJob<S,Void,O> init(int numTasks, List<S> setupInputs) {
        OJob<S,Void,O> job = new OJob<>(this);

        job.numWorkers = setupInputs.size();
        job.numTasks = numTasks;
        job.setupInputs = setupInputs;
        job.setupSender = setupSender;
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
     * [worker process] Repeatedly calls task and writes the output to stream
     *
     * The user cannot call or override this function.
     *
     * @throws Exception any exception the user code needs to throw
     */
    @Override
    final void taskWrapper() throws Exception {
        for (int i = getStartIndex(); i < getStartIndex() + getTaskSubset(); i++) {
            outputSender.write(task(i), oos);
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
     * should just use an OJob).
     *
     * @throws Exception any exception the user code needs to throw
     */
    public abstract void setup(S input) throws Exception;
    
    /**
     * Called repeatedly, once for each integer {@code i} in the index range
     * given by the command line arguments.
     *
     * @param i The integer index of the task. It should be used to calculate
     *          the initial state or values needed by the task.
     *          The task receives no other indication of which iteration it is.
     * @return The value to be sent back to the main process.
     * @throws Exception any exception the user code needs to throw
     */
    public abstract O task(int i) throws Exception;
}
