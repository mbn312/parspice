package parspice.dispatcher;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import parspice.rpc.ParSPICEGrpc;
import parspice.rpc.ParSPICEGrpc.ParSPICEStub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;


public class WorkerPool {

    private final WorkerPoolState _poolState;
    private static final Logger logger = Logger.getLogger(WorkerPool.class.getName());

    public WorkerPool(String ServerPath, int StartPort, int WorkerCount, int MaxBatchSize) throws IOException {
        int currentPort = StartPort;

        _poolState = new WorkerPoolState(
                WorkerCount,
                MaxBatchSize,
                new Process[WorkerCount],
                new Channel[WorkerCount],
                new ParSPICEStub[WorkerCount]);

        ManagedChannelBuilder<?> channelBuilder;
        for (int i = 0; i < WorkerCount; ++i)
        {
            int port = currentPort++;
            String args = "-p " + port;
            Process proc = Runtime.getRuntime().exec( ServerPath + args); // throws io exception
            _poolState.getProcessCollection()[i] = proc;

            channelBuilder = getChannelBuilder("127.0.0.1:", port);

            _poolState.getChannelCollection()[i] = channelBuilder.build();
            Channel channelI = _poolState.getChannelCollection()[i];
            _poolState.getParClients()[i] = ParSPICEGrpc.newStub(channelI);
        }
    }

    private ManagedChannelBuilder<?> getChannelBuilder(String host, int port) {
        return ManagedChannelBuilder.forAddress(host, port).usePlaintext();
    }


    /**
     * Distribute collection of parSpice tasks across worker processors.
     * This allows a large collection of tasks to be broken up across multiple worker for better throughput
     * @param <T_Request> Type of gRPC outbound request (ex: FurnshBundle)
     * @param <T_Response> Type of gRPC response object (ex: FurnshRep)
     * @throws Throwable
     */
    public <T_Request extends com.google.protobuf.MessageOrBuilder,
            T_Response extends com.google.protobuf.MessageOrBuilder>
    void PerformThreadedDistributedTask(DistributedTaskStateDistributable<T_Request, T_Response> Distributable) throws InterruptedException {

        DistributedTaskState<T_Request, T_Response> state = new DistributedTaskState<>(
                _poolState,
                Distributable);

        // TODID: start thread1worker and thread2worker pass in state
        CancellableThread<DistributedTaskState<T_Request, T_Response>> t1
                = new CancellableThread<DistributedTaskState<T_Request, T_Response>>(new DistributableTaskThreadWorker1<T_Request, T_Response>(), state);
        CancellableThread<DistributedTaskState<T_Request, T_Response>> t2
                = new CancellableThread<DistributedTaskState<T_Request, T_Response>>(new DistributableTaskThreadWorker2<T_Request, T_Response>(), state);
        t1.start();
        t2.start();

        state.getJobCompletionHandle().waitOne();
    }


    /**
     * Distribute collection of parSpice tasks across worker processors.
     * This allows a large collection of tasks to be broken up across multiple worker for better throughput
     * @param <T_Request> Type of gRPC outbound request (ex: FurnshBundle)
     * @param <T_Response> Type of gRPC response object (ex: FurnshRep)
     * @throws Throwable
     */
    public <T_Request extends com.google.protobuf.MessageOrBuilder,
            T_Response extends com.google.protobuf.MessageOrBuilder>
    void PerformDistributedTask(DistributedTaskStateDistributable<T_Request, T_Response> Distributable) throws Throwable {

        DistributedTaskState<T_Request, T_Response> state = new DistributedTaskState<>(
                _poolState,
                Distributable);

        final Channel[] channelCollection = state.getWorkerPoolState().getChannelCollection();
        final int maxBatchSize = state.getWorkerPoolState().getMaxBatchSize();
        final ParSPICEGrpc.ParSPICEStub[] parClients = state.getWorkerPoolState().getParClients();

        final ArrayList<AwaitableStreamObserver<T_Response>> currentTaskWave = state.getCurrentTaskWave();
        Boolean isBatchJobComplete = false;

        //continue working until the entire batch is processed
        while (!isBatchJobComplete)
        {
            for (int i = 0; i < channelCollection.length; ++i)
            {
                currentTaskWave.set(i, null);
            }

            //send batch cluster to each worker
            //send batches to each cluster at once
            for (int i = 0; i < channelCollection.length && !isBatchJobComplete; ++i)
            {
                DispatchRequest<T_Request> parRequest = Distributable.getNextRequest(maxBatchSize);
                isBatchJobComplete = parRequest.getCompletionState();

                //dont send request if the batch is empty
                if (parRequest.getSize() == 0)
                {
                    continue;
                }
                //send request to next worker
                //add awaitable to stack
                AwaitableStreamObserver<T_Response> requestTask = new AwaitableStreamObserver<>();
                Distributable.sendRequest(parClients[i], parRequest.getRequest(), requestTask);
                currentTaskWave.set(i, requestTask);
            }

            //wait for all worker responses to return before sending new batches
            //process the receipts, then repeat the process
            for (int i = 0; i < channelCollection.length; ++i)
            {
                AwaitableStreamObserver<T_Response> task = currentTaskWave.get(i);
                if (task == null)
                    continue;
                T_Response val = task.awaitValue();
                Distributable.responseCallback(val);
            }
        }
    }



    /**
     * Distribute collection of parSpice tasks across worker processors.
     * This allows a large collection of tasks to be broken up across multiple worker for better throughput
     * @param <T_Request> Type of gRPC outbound request (ex: FurnshBundle)
     * @param <T_Response> Type of gRPC response object (ex: FurnshRep)
     * @throws Throwable
     */
    public <T_Request extends com.google.protobuf.MessageOrBuilder,
            T_Response extends com.google.protobuf.MessageOrBuilder>
    void PerformBroadcastTask(
            DistributedTaskStateDistributable<T_Request, T_Response> Distributable) throws Throwable {

        DistributedTaskState<T_Request, T_Response> state = new DistributedTaskState<>(
                _poolState,
                Distributable);

        final Channel[] channelCollection = state.getWorkerPoolState().getChannelCollection();
        final int maxBatchSize = state.getWorkerPoolState().getMaxBatchSize();
        final ParSPICEStub[] parClients = state.getWorkerPoolState().getParClients();

        final ArrayList<AwaitableStreamObserver<T_Response>> currentTaskWave = state.getCurrentTaskWave();


        int currentIdx = 0;
        for (int i = 0; i < channelCollection.length; ++i)
        {
            currentTaskWave.set(i, null);
        }

        //make a new batch request to share across all workers
        DispatchRequest<T_Request> parRequest = Distributable.getNextRequest(maxBatchSize);

        //dont send request if the batch is empty
        if (parRequest.getSize() > 0) {
            //send batch cluster to each worker
            //send batches to each cluster at once
            for (int i = 0; i < channelCollection.length; ++i) {
                //send request to next worker
                //add awaitable to stack
                AwaitableStreamObserver<T_Response> requestTask = new AwaitableStreamObserver<>();
                Distributable.sendRequest(parClients[i], parRequest.getRequest(), requestTask);
                currentTaskWave.set(i, requestTask);
            }
        }

        //wait for all worker responses to return before sending new batches
        //process the receipts, then repeat the process
        for (int i = 0; i < channelCollection.length; ++i)
        {
            AwaitableStreamObserver<T_Response> task = currentTaskWave.get(i);
            if (task == null)
                continue;
            T_Response val = task.awaitValue();
            Distributable.responseCallback(val);
        }

    }

}
