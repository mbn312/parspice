package parspice.worker;

import parspice.ParSPICE;

/**
 * Jobs that do not produce output. The user can only get an instance of VoidJob
 * by calling init on a Worker that doesn't produce output.
 *
 * @param <S> Type for setup inputs (Void if none)
 * @param <I> Type for task inputs (Void if none)
 */
public class VoidJob<S,I> extends Job<S,I,Void> {
    VoidJob(Worker worker) {
        super(worker);
    }

    /**
     * Just calls Job.runCommon(par).
     *
     * @param par instance of ParSPICE to use.
     * @throws Exception
     */
    public void run(ParSPICE par) throws Exception {
        runCommon(par);
    }
}
