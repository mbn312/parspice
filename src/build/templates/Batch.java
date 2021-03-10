package parspice.functions.###UPPER_NAME###;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import parspice.Batch;
//import spice.basic.GFSearchUtils;
//import spice.basic.GFScalarQuantity;
import parspice.rpc.###UPPER_NAME###Request;
import parspice.rpc.###UPPER_NAME###Request.###UPPER_NAME###Input;
import parspice.rpc.###UPPER_NAME###Response;
import parspice.rpc.###UPPER_NAME###Response.###UPPER_NAME###Output;
import parspice.rpc.ParSPICEGrpc.ParSPICEStub;
import io.grpc.stub.StreamObserver;
import parspice.dispatcher.Distributable;

public class ###UPPER_NAME###Batch
        extends Batch<###UPPER_NAME###Call, ###UPPER_NAME###Response>
        implements Distributable<###UPPER_NAME###Request, ###UPPER_NAME###Response> {

    public void call(###ARGS###) {
        calls.add(new ###UPPER_NAME###Call(###ARGS_NO_TYPES###));
    }

    @Override
    public ###UPPER_NAME###Request nextRequest(int size) {
        ###UPPER_NAME###Request.Builder requestBuilder = ###UPPER_NAME###Request.newBuilder();
        int endIndex = unsentIndex + size;
        for (int i = unsentIndex; i < endIndex; i++) {
            requestBuilder.addInputs(calls.get(i).pack());
        }
        requestBuilder.setBatchID(unsentIndex);
        unsentIndex = endIndex;
        return requestBuilder.build();
    }

    @Override
    public void sendRequest(###UPPER_NAME###Request request, ParSPICEStub stub, StreamObserver<###UPPER_NAME###Response> awaiterTask) {
        stub.###LOWER_NAME###RPC(request, awaiterTask);
    }

    @Override
    public void receiveResponse(###UPPER_NAME###Response response) {
        List<###UPPER_NAME###Response.###UPPER_NAME###Output> outputs = response.getOutputsList();
        int startIndex = response.getBatchID();
        int endIndex = startIndex + outputs.size();
        for (int i = startIndex; i < endIndex; i++) {
            calls.get(i).unpack(outputs.get(i));
        }
    }
}