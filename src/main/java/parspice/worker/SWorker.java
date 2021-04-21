package parspice.worker;

import parspice.sender.Sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Superclass of all Workers that take inputs to the setup function,
 * but don't take task inputs or return task outputs.
 *
 * @param <S> The type given to the setup function by the main process.
 */
public abstract class SWorker<S> extends Worker {

    private final Sender<S> setupSender;

    private Socket inputSocket;
    private ObjectInputStream ois;

    public SWorker(Sender<S> setupSender) {
        this.setupSender = setupSender;
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
    public final VoidJob<S,Void> init(int numWorkers, int numTasks, S setupInput) {
        VoidJob<S,Void> job = new VoidJob<>(this);

        job.numWorkers = numWorkers;
        job.numTasks = numTasks;
        job.setupInputs = new ArrayList<S>(numWorkers);
        for (int i = 0; i < numWorkers; i++) {
            job.setupInputs.add(setupInput);
        }
        job.setupSender = setupSender;

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
    public final VoidJob<S,Void> init(int numTasks, List<S> setupInputs) {
        VoidJob<S,Void> job = new VoidJob<>(this);

        job.numWorkers = setupInputs.size();
        job.numTasks = numTasks;
        job.setupInputs = setupInputs;
        job.setupSender = setupSender;

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
