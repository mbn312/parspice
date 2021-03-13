package parspice;

import parspice.dispatcher.WorkerPool;

import java.util.ArrayList;

/**
 * A batch of NORMAL SPICE functions to be run concurrently.
 *
 * Subclasses of Batch are returned from {@link parspice.ParSPICE} batch factories, and should not be
 * instantiated directly or inherited from manually.
 *
 * Batches have a {@code call} function (declared in auto-generated subclasses) that take the same arguments as
 * their JNISPICE counterparts. After declaring all the calls in the batch, use the {@code run} function to
 * send and execute them. The results can then be accessed with the {@code get} function, or by using the batch
 * object as an iterator, as shown in the example below.
 *
 * Example: creating and running batches of scs2e and spkezr, modified from the In-Situ Sensing tutorial.
 *
 * <pre>
 *     {@code
 *          // create a ParSPICE engine instance.
 *          ParSPICE parspice = new ParSPICE();
 *
 *          parspice.furnsh('scvel.tm');
 *
 *          // call func1 several times.
 *          Scs2eBatch scs2d = parspice.scs2e();
 *          for (int i = 0; i < 1000; i++) {
 *              scs2e.call(-82, String.valueOf(1465674964.105 + i));
 *          }
 *          scs2e.run();
 *
 *          // access func1 results and use them for func2
 *          SpkezrBatch spkezr = parspice.spkezr();
 *          for (Scs2eCall scs2eCall : scs2e) {
 *              spkezr.call("CASSINI", scs2eCall.ret, "ECLIPJ2000", "NONE", "SUN", new double[6], new double[1]);
 *          }
 *          spkezr.run();
 *
 *          // use the results of spkezr in more functions, etc
 *     }
 * </pre>
 *
 *
 *
 * @param <C> The auto-generated Call subclass that this aggregates a list of.
 * @param <R> The Response gRPC struct that this receives from the workers.
 */
public abstract class Batch<C extends Call> {
    protected ArrayList<C> calls = new ArrayList<C>();
    protected int packIndex = 0;
    protected int unpackIndex = 0;
    protected WorkerPool pool;

    public Batch(WorkerPool pool) {
        this.pool = pool;
    }

    public abstract void run() throws Throwable;

    public C get(int index) {
        return calls.get(index);
    }
}
