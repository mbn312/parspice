package parspice.dispatcher;

import parspice.rpc.ParSPICEGrpc.ParSPICEStub;
import util.concurrent.CancellationToken;
import io.grpc.Channel;

import java.util.ArrayList;

public class DistributableTaskThreadWorker2<
        T_Request extends com.google.protobuf.MessageOrBuilder,
        T_Response extends com.google.protobuf.MessageOrBuilder> implements CancellableThreadRunnable<DistributedTaskState<T_Request, T_Response>> {

    public DistributableTaskThreadWorker2(){

    }

    @Override
    public void run(DistributedTaskState<T_Request, T_Response> state, CancellationToken token) throws Throwable {
        final Channel[] channelCollection = state.getWorkerPoolState().getChannelCollection();
        final int maxBatchSize = state.getWorkerPoolState().getMaxBatchSize();
        final ParSPICEStub[] parClients = state.getWorkerPoolState().getParClients();
        final AutoResetEvent requestWaitHandle = state.getRequestWaitHandle();
        final AutoResetEvent responseWaitHandle = state.getResponseWaitHandle();
        final ArrayList<AwaitableStreamObserver<T_Response>> currentTaskWave = state.getCurrentTaskWave();
        final DistributedTaskStateDistributable<T_Request, T_Response> distributable = state.getDistributable();

        //wait for batch responses
        //separates batch dispatching from result callback processing
        //allows one thread to push out new requests while this thread evaluates responses
        //improves performance with non-trivial callbacks
        int responseCount = 0;

        while (!state.isComplete() && responseCount < state.getCurrentTotalRequestCount() && !token.isCancellationRequested())
        {
            try {
                responseWaitHandle.waitOne(); // throws interrupted exception
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ArrayList<T_Response> resultClone = new ArrayList<>(channelCollection.length);

            //wait for all worker responses to return before sending new batches
            //process the receipts, then repeat the process
            //cache responses in 2nd buffer
            for (int i = 0; i < channelCollection.length; ++i)
            {
                AwaitableStreamObserver<T_Response> task = currentTaskWave.get(i);
                if (task == null)
                    continue;
                T_Response response = task.awaitValue();
                resultClone.add(response); // TODID wait for real response
                currentTaskWave.set(i, null);
            }
            //let requester thread send new wave of batches
            requestWaitHandle.set();
            //process response callbacks while new batch wave is being processed by workers
            for (int i = 0; i < channelCollection.length; ++i)
            {
                //TODO: add callback error handling to make sure that a bad handler wont break the whole system
                T_Response response = resultClone.get(i);
                if(response != null)
                {
                    distributable.responseCallback(response);
                }
                responseCount++;
            }
        }
        state.getJobCompletionHandle().set(); // TODID determine completion state
    }
}
