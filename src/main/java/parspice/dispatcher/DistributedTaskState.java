package parspice.dispatcher;

import java.util.ArrayList;

public class DistributedTaskState<
        T_Request extends com.google.protobuf.MessageOrBuilder,
        T_Response extends com.google.protobuf.MessageOrBuilder> {
    private WorkerPoolState poolState;
    private DistributedTaskStateDistributable<T_Request, T_Response> distributable;
    private AutoResetEvent requestWaitHandle;
    private AutoResetEvent responseWaitHandle;
    private ArrayList<AwaitableStreamObserver<T_Response>> currentTaskBatch;
    private AutoResetEvent jobCompletionHandle;
    private int currentTotalRequestCount = 2147483647; //int.max
    private boolean isJobComplete = false;

    public DistributedTaskState(
            WorkerPoolState PoolState,
            DistributedTaskStateDistributable<T_Request, T_Response> Distributable) {
        //TODO: add argument null checks
        this.poolState = PoolState;
        this.distributable = Distributable;
        this.currentTaskBatch = new ArrayList<AwaitableStreamObserver<T_Response>>();
        for(int i = 0; i < poolState.getWorkerCount(); ++i)
        {
            this.currentTaskBatch.add(null);
        }
        requestWaitHandle = new AutoResetEvent(true);
        responseWaitHandle = new AutoResetEvent(false);
        jobCompletionHandle = new AutoResetEvent(false);
    }

    public WorkerPoolState getWorkerPoolState()
    {
        return poolState;
    }

    public DistributedTaskStateDistributable<T_Request, T_Response> getDistributable() { return distributable; }

    public AutoResetEvent getRequestWaitHandle() {
        return requestWaitHandle;
    }

    public AutoResetEvent getResponseWaitHandle() {
        return responseWaitHandle;
    }

    public ArrayList<AwaitableStreamObserver<T_Response>> getCurrentTaskWave() {
        return currentTaskBatch;
    }

    public AutoResetEvent getJobCompletionHandle() {
        return jobCompletionHandle;
    }

    public boolean isComplete() { return isJobComplete; }

    public int getCurrentTotalRequestCount() { return currentTotalRequestCount; }

    public void setCompletionState(int requestCount)
    {
        currentTotalRequestCount = requestCount;
        isJobComplete = true;
    }
}
