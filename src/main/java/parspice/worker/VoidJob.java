package parspice.worker;

import parspice.ParSPICE;

public class VoidJob<S,I> extends Job<S,I,Void> {
    VoidJob(Worker<Void> worker) {
        super(worker);
    }

    public void run(ParSPICE par) throws Exception {
        runCommon(par);
    }
}
