package parspice.functions.###UPPER_NAME###;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import parspice.Batch;
import parspice.rpc.###UPPER_NAME###Request;
import parspice.rpc.###UPPER_NAME###Request.###UPPER_NAME###Input;
import parspice.rpc.###UPPER_NAME###Response;
import parspice.rpc.###UPPER_NAME###Response.###UPPER_NAME###Output;
import parspice.rpc.ParSPICEGrpc.ParSPICEStub;
import parspice.dispatcher.DistributedTaskStateDistributable;
import parspice.dispatcher.AwaitableStreamObserver;
import parspice.dispatcher.DispatchRequest;
import parspice.dispatcher.WorkerPool;

public class ###UPPER_NAME###Batch
        extends Batch<###UPPER_NAME###Call>
        implements DistributedTaskStateDistributable<###UPPER_NAME###Request, ###UPPER_NAME###Response> {

    public ###UPPER_NAME###Batch(WorkerPool pool) {
        super(pool);
    }

    public void call(###ARGS###) {
        calls.add(new ###UPPER_NAME###Call(###ARGS_NO_TYPES###));
    }

    @Override
    public void run() throws Throwable {
        pool.performDistributedTask(this);
    }

    @Override
    public DispatchRequest<###UPPER_NAME###Request> getNextRequest(int size) {
        ###UPPER_NAME###Request.Builder requestBuilder = ###UPPER_NAME###Request.newBuilder();
        int endIndex = packIndex + size;
        boolean lastRequest = false;
        if (endIndex >= calls.size()) {
            endIndex = calls.size();
            lastRequest = true;
        }
        for (int i = packIndex; i < endIndex; i++) {
            requestBuilder.addInputs(calls.get(i).pack());
        }
        packIndex = endIndex;
        ###UPPER_NAME###Request request = requestBuilder.build();
        return new DispatchRequest<###UPPER_NAME###Request>(request, size, lastRequest);
    }

    @Override
    public void sendRequest(ParSPICEStub stub, ###UPPER_NAME###Request request, AwaitableStreamObserver<###UPPER_NAME###Response> awaiterTask) {
        stub.###LOWER_NAME###RPC(request, awaiterTask);
    }

    @Override
    public void responseCallback(###UPPER_NAME###Response response) {
        List<###UPPER_NAME###Response.###UPPER_NAME###Output> outputs = response.getOutputsList();
        int size = outputs.size();
        int endIndex = unpackIndex + outputs.size();
        for (int i = 0; i < size; i++) {
            calls.get(i + unpackIndex).unpack(outputs.get(i));
        }
        unpackIndex = endIndex;
    }
}