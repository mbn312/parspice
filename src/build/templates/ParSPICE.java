package parspice;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import parspice.rpc.ParSPICEGrpc.ParSPICEFutureStub;
import parspice.rpc.ParSPICEGrpc.ParSPICEBlockingStub;
import spice.basic.CSPICE;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import parspice.rpc.RepeatedDouble;
import parspice.rpc.RepeatedInteger;
import parspice.dispatcher.WorkerPool;
import java.io.IOException;

###IMPORTS###

public class ParSPICE {
    // Still here for GLOBAL/TASK stateful functions. Will remove later.
    private ParSPICEBlockingStub blockingStub;

    private WorkerPool pool;

    String serverPath;
    int startPort;
    int workerCount;
    int maxBatchSize;

    /**
     * New ParSPICE object with worker pool arguments.
     *
     * @param serverPath location of the SpiceWorker.jar file
     * @param startPort lowest port number to be used by the workers
     * @param workerCount number of workers to use
     * @param maxBatchSize max number of CSPICE function calls to give to a worker at a time
     */
    public ParSPICE(String serverPath, int startPort, int workerCount, int maxBatchSize) {
        this.serverPath = serverPath;
        this.startPort = startPort;
        this.workerCount = workerCount;
        this.maxBatchSize = maxBatchSize;
    }

    public void start() throws IOException {
        stop();
        this.pool = new WorkerPool(serverPath, startPort, workerCount, maxBatchSize);
    }

    public void stop() {
        if (this.pool != null) this.pool.destroy();
    }

    /**
     * Generates factory (or wrapper) methods, one for each CSPICE function.
     *
     * All factory and wrapper methods are named exactly as they appear in JNISpice's CSPICE.java
     *
     * Types of functions: THESE ARE SUBJECT TO CHANGE
     * 1. NORMAL: functions with at least one input argument and at least one output that don't modify the state of the workers
     *     - example: spkpos reads from kernels, does calculations, returns output
     *     - ParSPICE provides a <NAME>Batch factory method with no arguments.
     * 2. STATELESS_NO_ARGS: stateless functions that have no arguments and always return the same output
     *     - example: clight returns the speed of light with no logic
     *     - ParSPICE provides a direct wrapper which executes the call locally and synchronously.
     *     - There is no reason to provide batch-request capability; so we do not include it
     *       to avoid confusion about what it does.
     *     - no Batch or Call objects associated with it
     * 3. WORKER_STATEFUL: stateful functions that modify the state of each individual worker
     *     - example: furnish loads a spice kernel which allows future NORMAL functions to use that data
     *     - ParSPICE provides an wrapper which executes the call once on each thread of the worker pool.
     *     - no Batch or Call objects associated with it
     * 4. STATEFUL: stateful functions that modify the state of the server as a whole
     *     - example: ckw02 generates a file and writes it.
     *     - example: daffna modifies an internal global variable in CSPICE
     *     - CURRENTLY WE HAVE NO DESIGN FOR THESE
     */
    ###FACTORIES###
}
