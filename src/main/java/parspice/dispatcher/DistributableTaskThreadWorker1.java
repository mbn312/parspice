package parspice.dispatcher;

import util.concurrent.CancellationToken;
import io.grpc.Channel;
import parspice.rpc.ParSPICEGrpc.ParSPICEStub;

import java.util.ArrayList;
import java.util.function.Function;

public class DistributableTaskThreadWorker1<
        T_Request extends com.google.protobuf.MessageOrBuilder,
        T_Response extends com.google.protobuf.MessageOrBuilder> implements CancellableThreadRunnable<DistributedTaskState<T_Request, T_Response>> {

    public DistributableTaskThreadWorker1(){
    }

    @Override
    public void run(DistributedTaskState<T_Request,  T_Response> state, CancellationToken token) {
        final Channel[] channelCollection = state.getWorkerPoolState().getChannelCollection();
        final int maxBatchSize = state.getWorkerPoolState().getMaxBatchSize();
        final ParSPICEStub[] parClients = state.getWorkerPoolState().getParClients();
        final AutoResetEvent requestWaitHandle = state.getRequestWaitHandle();
        final AutoResetEvent responseWaitHandle = state.getResponseWaitHandle();
        final ArrayList<AwaitableStreamObserver<T_Response>> currentTaskWave = state.getCurrentTaskWave();
        final DistributedTaskStateDistributable<T_Request, T_Response> distributable = state.getDistributable();

        int requestCount = 0;
        boolean isBatchJobComplete = false;

        //continue working until the entire batch is processed
        while (!isBatchJobComplete && !token.isCancellationRequested())
        {
            try {
                requestWaitHandle.waitOne(); // throws InterruptedException
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //send batch request to each worker
            //send all batch requests to the workers at once
            //the batches are sent out in waves
            //this allows the response processor to guarantee in-order evaluation

            for (int i = 0; i < channelCollection.length && !isBatchJobComplete; ++i)
            {
                //make a new batch request for this worker
                DispatchRequest<T_Request> parRequest = distributable.getNextRequest(maxBatchSize);
                isBatchJobComplete = parRequest.getCompletionState();

                //dont send request if the batch is empty
                if (parRequest.getSize() == 0)
                {
                    continue;
                }

                //send request to next worker
                //add awaitable to stack
                AwaitableStreamObserver<T_Response> requestTask = new AwaitableStreamObserver<>();
                distributable.sendRequest(parClients[i], parRequest.getRequest(), requestTask);
                currentTaskWave.set(i, requestTask);
                requestCount++;
            }
            responseWaitHandle.set();
        }
        //mark this job as completed once all requests are sent
        //this allows thread2 to terminate
        state.setCompletionState(requestCount);
    }
}
