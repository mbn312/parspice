package parspice.worker;

import parspice.ParSPICE;
import parspice.io.IOManager;

import java.util.ArrayList;

public class OJob<S,I,O> extends Job<S,I,O> {
    OJob(Worker<O> worker) {
        super(worker);
    }

    public ArrayList<O> run(ParSPICE par) throws Exception {
        runCommon(par);

        ArrayList<O> results = ioManagers.get(0).getOutputs();
        if (results == null) {
            return null;
        }
        results.ensureCapacity(numTasks);
        for (IOManager<S, I, O> ioManager : ioManagers.subList(1, ioManagers.size())) {
            results.addAll(ioManager.getOutputs());
        }
        return results;
    }
}
